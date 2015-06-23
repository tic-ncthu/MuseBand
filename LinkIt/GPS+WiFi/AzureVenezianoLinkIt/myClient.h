#ifndef myClient_h
#define myClient_h

#include <Arduino.h>
#include <LWiFiClient.h>
#include <ArduinoJson.h>

#define REST_Read "GET"
#define REST_Create "POST"
#define REST_Update "PATCH"

#define APP_KEY "pVEkAZkXlQVEWgPYRxnoZeEfBwNlvQ43"
#define MASTER_KEY "ufuucXwiZbcKWOlGYvRkHcFkrzJelZ57"
#define SITE_URL "andytic.azure-mobile.net"

class myClient{
protected:
	LWiFiClient *client;
public:
	myClient(LWiFiClient &c);
	JsonObject& sendRequest(const char* subUrl, const char* method, JsonObject* data);
};

#endif