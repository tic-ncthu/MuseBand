package nctuxnthu.museband;

import android.app.AlertDialog;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.table.TableOperationCallback;
import com.microsoft.windowsazure.notifications.NotificationsManager;

import java.net.MalformedURLException;

public class Museband extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private EditText boxLa;
    private EditText boxLo;
    private double latitude;
    private double longitude;
    private Button btnGo;
    private Switch switchGPS;

    public static MobileServiceClient mClient;

    /**
     * Mobile Service Table used to access data
     */
    private MobileServiceTable<GPSDataItem> GPSTable;

    /**
     * Adapter to sync the items list with the view
     */
    public static final String PROJECT_ID = "269342721";
    //private TextView textBox;

    /**
     * Initializes the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_museband);

        boxLa = (EditText)findViewById(R.id.boxLa);
        boxLo = (EditText)findViewById(R.id.boxLo);
        btnGo = (Button)findViewById(R.id.go);
        switchGPS = (Switch) findViewById(R.id.switchGPS);
/*
            setUpMapIfNeeded();
                if (mMap != null) {
                setUpMap(0, 0);
            }
*/

        try {
            // Create the Mobile Service Client instance, using the provided
            // Mobile Service URL and key
            mClient = new MobileServiceClient(
                    "https://andytic.azure-mobile.net/",
                    "pVEkAZkXlQVEWgPYRxnoZeEfBwNlvQ43",
                    this);

            NotificationsManager.handleNotifications(this, PROJECT_ID, MyPushNotificationsHandler.class);

            GPSDataItem gpsItem = new GPSDataItem();
            gpsItem.gps_Id = "test1";
            gpsItem.gps_status = "0";
            gpsItem.gps_longtitude = 24.786134;
            gpsItem.gps_latitude = 120.966724;

            GPSTable = mClient.getTable(GPSDataItem.class);
            GPSTable.insert(gpsItem, new TableOperationCallback<GPSDataItem>() {
                @Override
                public void onCompleted(GPSDataItem entity, Exception exception, ServiceFilterResponse response) {
                    if (exception == null) ;
                        // Success
                    else ;
                    // Failed
                }
            });

        } catch (MalformedURLException e) {
            createAndShowDialog(new Exception("There was an error creating the Mobile Service. Verify the URL"), "Error");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        switchGPS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    setUpMapIfNeeded();
                    btnGo.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            latitude = Double.parseDouble(boxLa.getText().toString());
                            longitude = Double.parseDouble(boxLo.getText().toString());
                            if (mMap != null) {
                                setUpMap(latitude, longitude);
                            }
                        }
                    });
                } else {
                    mMap = null;
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
    private void setUpMap(double latitude, double longitude) {
        LatLng place = new LatLng(latitude,longitude);
        moveMap(place);
        addMarker(place, "NCTU");
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
}

