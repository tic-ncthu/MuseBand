/*
*	Copyright by Andy Liu
*	Created to connect Azure by LinkIt One board
*/
#include <LWiFi.h>
#include <LWiFiClient.h>
#include <LGPS.h>
#include <ArduinoJson.h>
#include "myClient.h"
#include <LTask.h>

/* Macros definition */
#define WIFINAME "214"
#define WIFIPWD "0976595363"
#define APP_KEY "pVEkAZkXlQVEWgPYRxnoZeEfBwNlvQ43"
#define MASTER_KEY "ufuucXwiZbcKWOlGYvRkHcFkrzJelZ57"
#define SITE_URL "andytic.azure-mobile.net"
#define WIFI_AUTH LWIFI_WPA  // choose from LWIFI_OPEN, LWIFI_WPA, or LWIFI_WEP.
#define DEVICE_ID "demo1"

#define REST_Create "POST"
#define REST_Read "GET"
#define REST_Update "PATCH"

int _wifiStatus;
int lastStatus = 0;

gpsSentenceInfoStruct _gpsinfo;

LWiFiClient c;
myClient myC(c);

void setup()
{
	// Setup Serial
    Serial.begin(9600);

    // Turn on the GPS sensor
    LGPS.powerOn();

    // Setup the Wi-Fi connection
    LWiFi.begin();
    _wifiStatus = LWiFi.connect(WIFINAME, LWiFiLoginInfo(WIFI_AUTH, WIFIPWD));

	Serial.print("Waiting for input");
}

void loop()
{
	// Wait for serial input to start
	Serial.print(".");
	delay(1000);
	if(Serial.available() > 0)
	{
		Serial.println("\nStart Connecting");
		
		if (_wifiStatus >= 0)
		{
			// Keep GET until status = 1
			String currentStatus;
			JsonObject& objRet = myC.sendRequest("/tables/GPSDataItem/" DEVICE_ID, REST_Read, NULL);
			const char* objStatus = objRet["status"];
			currentStatus = objStatus;
			Serial.print("Current Status: ");
			Serial.println(currentStatus);
			
			if(currentStatus == "1" || (lastStatus == 1 && currentStatus == "0"))
			{
				// Turn GPS on
				LGPS.getData(&_gpsinfo);
				String gpsData = (char*)_gpsinfo.GPGGA;
				gpsData = gpsData.substring(18, 42);
				String longitudeString = gpsData.substring(0, 2) + "." + gpsData.substring(2, 4) + gpsData.substring(5, 9);
				String latitudeString = gpsData.substring(12, 15) + "." + gpsData.substring(15, 17) + gpsData.substring(18, 22);
				
				float latitude = latitudeString.toFloat();
				float longitude = longitudeString.toFloat();
				
				Serial.print("Longitude: ");
				Serial.println(longitude);
				Serial.print("Latitude: ");
				Serial.println(latitude);
				
				// PATCH GPS data
				StaticJsonBuffer<4096> jsonBuffer;
				JsonObject& jobj = jsonBuffer.createObject();
				jobj["id"] = "demo1";
				jobj["latitude"] = latitude;
				jobj["longtitude"] = longitude;
				
				if(lastStatus == 0 && currentStatus == "1")
					lastStatus = 1;
				else if(lastStatus == 1 && currentStatus == "0")
					lastStatus = 0;
				
				JsonObject& objRet = myC.sendRequest("/tables/GPSDataItem/" DEVICE_ID, REST_Update, &jobj);
			}
			
			// Wait for 10 seconds before restart
			delay(10000);
		}	// End of WiFi available
	}	// End of Serial available
}
