#include <ESP8266WiFi.h>
#include <ESP8266WebServer.h>
#include <ArduinoOTA.h>
#include "definitions.h"
#include "constants.h"
#include "config_utils.h"

IPAddress AP_IP = IPAddress(10, 1, 1, 1);
IPAddress AP_subnet = IPAddress(255, 255, 255, 0);

WifiConf wifiConf;
bool cold_start = false;

// Web server for editing configuration.
// 80 is the default http port.
ESP8266WebServer server(1880);

static const uint8_t GATE_PIN = D1;
static const uint8_t DOOR_PIN = D2;

bool connectToWiFi();
void setUpAccessPoint();
void setUpWebServer(bool coldStart);
void setUpOverTheAirProgramming(bool coldStart);

void handleColdStateWebServerRequest();
void handleStateGet();
void handleChangeStateGate();
void handleChageStateDoor();

void setup()
{
  Serial.begin(9600);
  Serial.println("Booting...");
  pinMode(LED_BUILTIN, OUTPUT);
  pinMode(GATE_PIN, OUTPUT);
  pinMode(DOOR_PIN, OUTPUT);

  // init wifiConfig
  // to read/write wifi configuration.
  initConf(&wifiConf);
  readWifiConf(&wifiConf);
  Serial.printf("Try connect to configured accesspoint \"%s\" \n", wifiConf.wifi_ssid);
  Serial.printf("Webserver username \"%s\" \n", wifiConf.ws_username);

  if (!connectToWiFi())
  {
    cold_start = true;
    setUpAccessPoint();
  }
  setUpWebServer(cold_start);
  setUpOverTheAirProgramming(cold_start);
  digitalWrite(LED_BUILTIN, HIGH);
  Serial.printf("Cold start %d \n", cold_start);
  Serial.println("Setup completed ...");

}

bool connectToWiFi()
{
  Serial.printf("Connecting to '%s'\n", wifiConf.wifi_ssid);

  WiFi.mode(WIFI_STA);
  WiFi.begin(wifiConf.wifi_ssid, wifiConf.wifi_password);
  if (WiFi.waitForConnectResult() == WL_CONNECTED)
  {
    Serial.print("Connected. IP: ");
    Serial.println(WiFi.localIP());
    return true;
  }
  else
  {
    Serial.println("Connection Failed!");
    return false;
  }
}

void setUpAccessPoint()
{
  Serial.println("Setting up access point.");
  Serial.printf("SSID: %s\n", AP_ssid);
  Serial.printf("Password: %s\n", AP_password);

  WiFi.mode(WIFI_AP_STA);
  WiFi.softAPConfig(AP_IP, AP_IP, AP_subnet);
  if (WiFi.softAP(AP_ssid, AP_password))
  {
    Serial.print("Ready. Access point IP: ");
    Serial.println(WiFi.softAPIP());
  }
  else
  {
    Serial.println("Setting up access point failed!");
  }
}

void setUpWebServer(bool coldStart)
{
  if (coldStart)
    server.on("/", handleColdStateWebServerRequest);
  else
  {
    server.on("/home/state", HTTP_GET, handleStateGet);
    server.on("/home/gate", HTTP_PUT, handleChangeStateGate);
    server.on("/home/door", HTTP_PUT, handleChageStateDoor);
  }
  server.begin();
}

void handleColdStateWebServerRequest()
{
  bool save = false;

  if (server.hasArg("ssid") && server.hasArg("password") && server.hasArg("ws_user") && server.hasArg("ws_password"))
  {
    server.arg("ssid").toCharArray(wifiConf.wifi_ssid, sizeof(wifiConf.wifi_ssid));
    server.arg("password").toCharArray(wifiConf.wifi_password, sizeof(wifiConf.wifi_password));
    server.arg("ws_user").toCharArray(wifiConf.ws_username, sizeof(wifiConf.ws_username));
    server.arg("ws_password").toCharArray(wifiConf.ws_password, sizeof(wifiConf.ws_password));
    server.arg("ota_password").toCharArray(wifiConf.ota_password, sizeof(wifiConf.ota_password));

    Serial.println(server.arg("ssid"));
    Serial.println(wifiConf.wifi_ssid);

    Serial.println(server.arg("ws_user"));
    Serial.println(wifiConf.ws_username);

    writeWifiConf(&wifiConf);
    save = true;
  }

  String message = "";
  message += DOC_HEADER;
  if (save)
    message += DOC_REBOOTING;
  else
  {
    String form = String(DOC_FORM);
    form.replace("$SSID", wifiConf.wifi_ssid);
    form.replace("$PASSWORD", wifiConf.wifi_password);
    form.replace("$WSUSER", wifiConf.ws_username);
    form.replace("$WSPASSORD", wifiConf.ws_password);
    form.replace("$OTAPASSORD", wifiConf.ota_password);
    message += form;
  }

  message += DOC_FOOTER;
  server.send(200, "text/html", message);

  if (save)
  {
    Serial.println("Wi-Fi conf saved. Rebooting...");
    delay(1000);
    ESP.restart();
  }
}

void handleStateGet()
{
  String message = STATE_OK_RESP;
  server.send(200, "application/json", message);
}

void handleChangeStateGate()
{
  if (!server.authenticate(wifiConf.ws_username, wifiConf.ws_password))
  {
    return server.requestAuthentication();
  }
  String message = STATE_CHANGED_GATE_RESP;
  server.send(200, "application/json", message);
  digitalWrite(GATE_PIN, HIGH);
  digitalWrite(LED_BUILTIN, LOW);  // turn the LED on (LOW is the voltage level)
  delay(1000);                      // wait for a second
  digitalWrite(GATE_PIN, LOW);
  digitalWrite(LED_BUILTIN, HIGH); // turn the LED off by making the voltage HIGH
}

void handleChageStateDoor()
{
  if (!server.authenticate(wifiConf.ws_username, wifiConf.ws_password))
  {
    return server.requestAuthentication();
  }
  String message = STATE_CHANGED_DOOR_RESP;
  server.send(200, "application/json", message);
  digitalWrite(DOOR_PIN, HIGH);
  digitalWrite(LED_BUILTIN, LOW);  // turn the LED on (LOW is the voltage level)
  delay(1000);                     // wait for a second
  digitalWrite(DOOR_PIN, LOW);
  digitalWrite(LED_BUILTIN, HIGH); // turn the LED off by making the voltage HIGH

}

void setUpOverTheAirProgramming(bool coldStart)
{

  // Change OTA port.
  // Default: 8266
  // ArduinoOTA.setPort(8266);

  // Change the name of how it is going to
  // show up in Arduino IDE.
  // Default: esp8266-[ChipID]
  // ArduinoOTA.setHostname("myesp8266");

  // Re-programming passowrd.
  // No password by default.
  if (!cold_start){
    ArduinoOTA.setPassword(wifiConf.ota_password);
  }

  ArduinoOTA.begin();
}

void loop()
{
  // Give processing time for ArduinoOTA.
  // This must be called regularly
  // for the Over-The-Air upload to work.
  ArduinoOTA.handle();

  // Give processing time for the webserver.
  // This must be called regularly
  // for the webserver to work.
  server.handleClient();
  if (cold_start){
    long int since = millis();
    if (since > 3*60*1000){
      Serial.println("More than 3 minutes without any chnages in wifi settings. Rebooting...");
      delay(1000);
      ESP.restart();
    }
  }
  
}