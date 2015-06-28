package nctuxnthu.museband;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.wearable.DataItem;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceList;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.table.TableOperationCallback;
import com.microsoft.windowsazure.notifications.NotificationsManager;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class Museband extends FragmentActivity {


    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private String MusebandId;
    private EditText boxId;
    private TextView boxLa;
    private TextView boxLo;
    private Switch switchGPS;
    private boolean valid;

    public static MobileServiceClient mClient;

    /**
     * Mobile Service Table used to access data
     */
    private MobileServiceTable<GPSDataItem> GPSTable;

    private MobileServiceTable<FacilityDataItem> FacilityTable;

    private GPSDataItem gpsItem = new GPSDataItem();

    private FacilityDataItem facilityItem = new FacilityDataItem();

    public static final String PROJECT_ID = "269342721";
    //private TextView textBox;

    /**
     * Initializes the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_museband);
        Log.d("Print", "oncreate");
        MusebandId = null;
        valid = false;
        boxId = (EditText)findViewById(R.id.boxId);
        boxLa = (TextView)findViewById(R.id.boxLa);
        boxLo = (TextView)findViewById(R.id.boxLo);
//        btnGo = (Button)findViewById(R.id.go);
        switchGPS = (Switch) findViewById(R.id.switchGPS);

        setUpMapIfNeeded();
        if (mMap != null) {
            //setUpMap(0, 0);
            getFacilityItem();
        }


        LatLng place = new LatLng(24.786167 , 120.966778);
        moveMap(place);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(place)
                .title("f1")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                /*.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(),
                        R.drawable.facility)));*/

        //mMap.addMarker(markerOptions);

        try {
            // Create the Mobile Service Client instance, using the provided
            // Mobile Service URL and key
            mClient = new MobileServiceClient(
                    "https://andytic.azure-mobile.net/",
                    "pVEkAZkXlQVEWgPYRxnoZeEfBwNlvQ43",
                    this);
            GPSTable = mClient.getTable(GPSDataItem.class);
            FacilityTable = mClient.getTable(FacilityDataItem.class);

            NotificationsManager.handleNotifications(this, PROJECT_ID, MyPushNotificationsHandler.class);

        } catch (MalformedURLException e) {
            createAndShowDialog(new Exception("There was an error creating the Mobile Service. Verify the URL"), "Error");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("Print", "onresume");
        switchGPS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Log.d("Print", "ischeck");
                    if (MusebandId == null) {
                        MusebandId = boxId.getText().toString();
                    }
                    getGPSDataItem();
                } else {
                    boxLa.setText("");
                    boxLo.setText("");
                    if (valid) {
                        gpsItem.gps_status = "0";
                        updateGPSDataItem();
                    }
                    MusebandId = null;
                    //mMap = null;
                }
            }
        });
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap(Number latitude, Number longitude) {
        LatLng place = new LatLng(latitude.doubleValue(),longitude.doubleValue());
        moveMap(place);
        //addMarker(place, MusebandId);
        addMarker(place, "You are Here!");
    }

    private void moveMap(LatLng place){
        CameraPosition cameraPosition =
                new CameraPosition.Builder()
                .target(place)
                .zoom(17)
                .build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private void addMarker(LatLng place, String title){
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(place)
                     .title(title);
        mMap.addMarker(markerOptions);
    }

    /**
     * Creates a dialog and shows it
     *
     * @param exception
     *            The exception to show in the dialog
     * @param title
     *            The dialog title
     */
    private void createAndShowDialog(Exception exception, String title) {
        Throwable ex = exception;
        if(exception.getCause() != null){
            ex = exception.getCause();
        }
        createAndShowDialog(ex.getMessage(), title);
    }

    /**
     * Creates a dialog and shows it
     *
     * @param message
     *            The dialog message
     * @param title
     *            The dialog title
     */
    private void createAndShowDialog(final String message, final String title) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(message);
        builder.setTitle(title);
        builder.create().show();
    }

    /**
     * Refresh the list with the GPSDataItem in the Mobile Service Table
     */
    private void getGPSDataItem() {

        new LoadGPSAsyncTask(){
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    Log.d("Print", "getItem: ");
                    final GPSDataItem item = GPSTable.lookUp(MusebandId).get();
                    valid = true;
                    Log.d("Print", "Itemget");
                    Log.d("Print", "id: " + item.gps_Id);
                    Log.d("Print", "latitude: " + item.gps_latitude);
                    Log.d("Print", "longitude: " + item.gps_longtitude);
                    gpsItem.gps_Id = item.gps_Id;
                    gpsItem.gps_status = "1";
                    gpsItem.gps_latitude = item.gps_latitude;
                    gpsItem.gps_longtitude = item.gps_longtitude;

                } catch (Exception exception) {
                    //createAndShowDialog(exception, "Error");
                    valid = false;
                    Log.d("Print", "Item not found");
                }
                return null;
            }
            protected void onPostExecute(Void unused){
                if (!valid){
                    switchGPS.setChecked(false);
                }
                else{
                    Log.d("Print", "set location");
                    boxLa.setText(gpsItem.gps_latitude.toString());
                    boxLo.setText(gpsItem.gps_longtitude.toString());
                    Log.d("Print", "set location done");

                    setUpMapIfNeeded();
                    if (mMap != null) {
                        setUpMap(gpsItem.gps_latitude, gpsItem.gps_longtitude);
                    }
                }
            }
        }.execute();
    }

    public void updateGPSDataItem() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    Log.d("Print", "update");
                    GPSTable.update(gpsItem).get();
                    Log.d("Print", "updata done");

                } catch (Exception e) {
                    createAndShowDialog(e, "Error");
                }
                return null;
            }
        }.execute();
    }

    public void getFacilityItem() {
        new AsyncTask<Void,Void,Void>(){
            @Override
            protected Void doInBackground(Void... params){
                try {
                    Log.d("Print", "setupFacilities");
                    //final List<FacilityDataItem> results = FacilityTable.execute().get();
                    final FacilityDataItem item = FacilityTable.lookUp("fatAndy").get();
                    Log.d("Print", "FacilityListGet ");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //for (FacilityDataItem item : results) {
                                if(mMap != null){
                                    LatLng place = new LatLng(item.latitude.doubleValue(),item.longtitude.doubleValue());
                                    String title = facilityItem.Id;
                                    MarkerOptions markerOptions = new MarkerOptions();
                                    markerOptions.position(place)
                                            .title(title);
                                    mMap.addMarker(markerOptions);
                                    moveMap(place);
                                }
                            //}
                        }
                    });
                } catch (Exception exception) {
                    //createAndShowDialog(exception, "Error");
                    Log.d("Print", "Facility setup error");
                }
                return null;
            }
            protected void onPostExecute(Void unused){

            }
        }.execute();
    }

}

