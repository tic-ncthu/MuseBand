#include "myClient.h"

myClient::myClient(LWiFiClient &c)
{
	client = &c;
}

JsonObject& myClient::sendRequest(const char* subUrl, const char* method, JsonObject* data)
{
	String readString;
	StaticJsonBuffer<4096> jsonBuffer;
	
	while(client->connect(SITE_URL, 80) == 0)
	{
		Serial.println("Reconnecting to server");
		delay(1000);
	}
	
	if(method == REST_Read)
	{
		Serial.println("Start sending GET request");
		
		// HTTP request header
		client->print("GET ");
		client->print(subUrl);
		client->println(" HTTP/1.1");
		client->println("Accept: application/json");
		client->println("X-ZUMO-APPLICATION: " APP_KEY);
		client->println("X-ZUMO-MASTER: " MASTER_KEY);
		client->println("Host: " SITE_URL);
		client->println("Connection: close");
		client->println();
		
		Serial.println("Waiting for response");
		while(!client->available())
			delay(1000);
		
		int flag = 0;
		
		while(client)
		{
			// HTTP response header
			int v = client->read();
			char c = (char)v;
			if(v != -1)
			{
				if(c != '\n')
					readString += (char)v;
				else
				{
					Serial.println(readString);
					readString = "";
				}
			}
			else
			{
				// HTTP response body
				Serial.println(readString);
				Serial.println("No more content, disconnect");
				client->println("Connection: close");
				client->stop();
				
				// Parse body to JsonObject
				char buf[4096];
				strcpy(buf, readString.c_str());
				JsonObject& parseResult = jsonBuffer.parseObject(buf);
				JsonObject* objRet = &parseResult;
				
				if(parseResult == parseResult.invalid())
					Serial.println("Json Object parse failed");
				else
				{
					const char* objId = parseResult["id"];
					return *objRet;
				}
				break;
			}
		}
	}	// End of REST_Read
	else if(method == REST_Update)
	{
		char buffer[1024];
		int length = data->printTo(buffer, sizeof(buffer));
		
		Serial.println("Start sending PATCH request");
		
		// HTTP request header
		client->print("PATCH ");
		client->print(subUrl);
		client->println(" HTTP/1.1");
		client->println("Accept: application/json");
		client->println("X-ZUMO-APPLICATION: " APP_KEY);
		client->println("X-ZUMO-MASTER: " MASTER_KEY);
		client->println("Content-Type: application/json");
		client->print("Content-Length: ");
		client->println(length);
		client->println("Host: " SITE_URL);
		client->println();
		
		// HTTP request body
		client->write((uint8_t*)&buffer, length);
		client->println();
		
		Serial.println("Waiting for response");
		while(!client->available())
			delay(1000);
		
		while(client)
		{
			// HTTP response header
			int v = client->read();
			char c = (char)v;
			if(v != -1)
			{
				if(c != '\n')
					readString += (char)v;
				else
				{
					Serial.println(readString);
					readString = "";
				}
			}
			else
			{
				// HTTP response body
				Serial.println(readString);
				Serial.println("No more content, disconnect");
				client->stop();
				char buf[4096];
				strcpy(buf, readString.c_str());
				
				// Parse body to JsonObject
				JsonObject& parseResult = jsonBuffer.parseObject(buf);
				JsonObject* objRet = &parseResult;
				if(parseResult == parseResult.invalid())
					Serial.println("Json Object parse failed");
				else
				{
					const char* objId = parseResult["id"];
					return *objRet;
				}
				break;
			}
		}
	}
	else
	{
		char buffer[1024];
		int length = data->printTo(buffer, sizeof(buffer));
		
		Serial.println("Start sending POST request");
		
		// HTTP request header
		client->print("POST ");
		client->print(subUrl);
		client->println(" HTTP/1.1");
		client->println("Accept: application/json");
		client->println("X-ZUMO-APPLICATION: " APP_KEY);
		client->println("X-ZUMO-MASTER: " MASTER_KEY);
		client->println("Content-Type: application/json");
		client->print("Content-Length: ");
		client->println(length);
		client->println("Host: " SITE_URL);
		client->println();
		
		// HTTP request body
		client->write((uint8_t*)&buffer, length);
		client->println();
		
		Serial.println("Waiting for response");
		while(!client->available())
			delay(1000);
		
		while(client)
		{
			// HTTP response header
			int v = client->read();
			char c = (char)v;
			if(v != -1)
			{
				if(c != '\n')
					readString += (char)v;
				else
				{
					Serial.println(readString);
					readString = "";
				}
			}
			else
			{
				// HTTP response body
				Serial.println("No more content, disconnect");
				client->stop();
				char buf[4096];
				strcpy(buf, readString.c_str());
				
				// Parse body to JsonObject
				JsonObject& parseResult = jsonBuffer.parseObject(buf);
				JsonObject* objRet = &parseResult;
				if(parseResult == parseResult.invalid())
					Serial.println("Json Object parse failed");
				else
				{
					const char* objId = parseResult["id"];
					return *objRet;
				}
				break;
			}
		}
	}
}