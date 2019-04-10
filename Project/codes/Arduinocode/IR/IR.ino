#include <ESP8266WiFi.h>
#include <ArduinoJson.h>
#include <FirebaseArduino.h>

#define FIREBASE_HOST "host url"
#define FIREBASE_AUTH "firebase secret"
#define WIFI_SSID "ssid"
#define WIFI_PASSWORD "pass"

String chipId = "12345";

int inputPin1 = D0;  
int val1 = 0;

int inputPin2 = D1;  
int val2 = 0;

void setupFirebase() 
{
  Firebase.begin(FIREBASE_HOST,FIREBASE_AUTH);
}

void setupWifi() 
{
  WiFi.begin(WIFI_SSID , WIFI_PASSWORD);
  Serial.println("Hey i 'm connecting...");
  while (WiFi.status() != WL_CONNECTED)
  {
    Serial.print(".");
    delay(500);
  }
  Serial.println();
  Serial.println("I 'm connected and my IP address: ");
  Serial.println(WiFi.localIP());
}

void setup() 
{ 
  Serial.begin(9600);
  setupWifi();
  setupFirebase();
  pinMode(inputPin1, INPUT);
  pinMode(inputPin2, INPUT); 
} 

void loop()
{ 
   String path = "Devices/12345/Slots";
   val1 = digitalRead(inputPin1);
   val2 = digitalRead(inputPin2); 
   if (val1 == HIGH)
   {
     Firebase.setBool(path + "/Slot1", true); 
         Serial.println("1");  
   } 
   else 
   { 
      Firebase.setBool(path + "/Slot1",false); 
      Serial.println("0");
   }
   
   if (val2 == HIGH)
   {
      Firebase.setBool(path + "/Slot2",true);   
       Serial.println("1");  
   } 
   else 
   { 
      Firebase.setBool(path + "/Slot2",false); 
      Serial.println("0");
   } 
}
