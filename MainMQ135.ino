#if defined(ESP32)
#include <WiFi.h>
#include <FirebaseESP32.h>
#elif defined(ESP8266)
#include <ESP8266WiFi.h>
#include <FirebaseESP8266.h>
#include <WiFiUdp.h>
#include <math.h>
//#include <MQ135.h>
#endif

// Provide the token generation process info.
#include <addons/TokenHelper.h>

// Provide the RTDB payload printing info and other helper functions.
#include <addons/RTDBHelper.h>

//#define PIN_MQ135 A0
//MQ135 mq135_sensor(PIN_MQ135);
/* 1. Define the WiFi credentials */
#define WIFI_SSID "abcd"
#define WIFI_PASSWORD "12345678"

// For the following credentials, see examples/Authentications/SignInAsUser/EmailPassword/EmailPassword.ino

/* 2. Define the API Key */
#define API_KEY "AIzaSyD8K0NlFP9ZaaD1DmWqJsSHTJWx0VCedlA"

/* 3. Define the RTDB URL */
#define DATABASE_URL "safebreathe-8b17d-default-rtdb.firebaseio.com" //<databaseName>.firebaseio.com or <databaseName>.<region>.firebasedatabase.app

/* 4. Define the user Email and password that alreadey registerd or added in your project */
#define USER_EMAIL "gautamverma01.gv@gmail.com"
#define USER_PASSWORD "Abcd@1234"

float m = -0.3376; //Slope
float b = 0.7165; //Y-Intercept
//const long utcOffsetInSeconds = 25200;
//WiFiUDP ntpUDP;
//NTPClient timeClient(ntpUDP, "pool.ntp.org", utcOffsetInSeconds);

// Define Firebase Data object
FirebaseData fbdo;

FirebaseAuth auth;
FirebaseConfig config;

unsigned long sendDataPrevMillis = 0;

void setup()
{
  Serial.begin(115200);
  pinMode(A0, INPUT); //new

  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  Serial.print("Connecting to Wi-Fi");
  while (WiFi.status() != WL_CONNECTED)
  {
    Serial.print(".");
    delay(300);
  }
  Serial.println();
  Serial.print("Connected with IP: ");
  Serial.println(WiFi.localIP());
  Serial.println();

//  timeClient.begin();

  Serial.printf("Firebase Client v%s\n\n", FIREBASE_CLIENT_VERSION);

  /* Assign the api key (required) */
  config.api_key = API_KEY;

  /* Assign the user sign in credentials */
  auth.user.email = USER_EMAIL;
  auth.user.password = USER_PASSWORD;

  /* Assign the RTDB URL (required) */
  config.database_url = DATABASE_URL;

  /* Assign the callback function for the long running token generation task */
  config.token_status_callback = tokenStatusCallback; // see addons/TokenHelper.h

  Firebase.begin(&config, &auth);

  // Comment or pass false value when WiFi reconnection will control by your code or third party library
  Firebase.reconnectWiFi(true);

  Firebase.setDoubleDigits(2);
}

void loop()
{

  float ratio = (((5.0 * 10.0) / (analogRead(A0) * (5.0 / 1023.0))) - 10.0) / ((((5.0 * 10.0) / (analogRead(A0) * (5.0 / 1023.0))) - 10.0) / 3.7);
//
  float ppm_log = (log10(ratio) - b) / m; //Get ppm value in linear scale according to the the ratio value
  float ppm = pow(10, ppm_log); //Convert ppm value to log scale


  Firebase.ready(); // should be called repeatedly to handle authentication tasks.
  if (Firebase.ready() && (millis() - sendDataPrevMillis > 1500 || sendDataPrevMillis == 0))
  {
    sendDataPrevMillis = millis();

    //    Serial.printf("Set double... %s\n", Firebase.setDouble(fbdo, F("/mq135"), ppm) ? "ok" : fbdo.errorReason().c_str());
    //
    //    Serial.printf("Get double... %s\n", Firebase.getDouble(fbdo, F("/mq135")) ? String(fbdo.to<double>()).c_str() : fbdo.errorReason().c_str());

    //    Firebase.setDouble(fbdo, F("/mq135"), ppm);
//    Firebase.setString(fbdo, F("/time"), TimeNow);
    Firebase.setInt(fbdo, F("/mq135"), round(ppm));   // replace analogRead with ppm
    //    Firebase.getDouble(fbdo, F("/mq135"));
//    Serial.println(TimeNow);
    Serial.println(ppm);    // replace analogRead with ppm

  }
}
