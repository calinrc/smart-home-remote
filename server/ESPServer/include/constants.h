#ifndef __CONSTANTS_H_
#define __CONSTANTS_H_

// html documents
const char* const DOC_HEADER = "<!DOCTYPE html><html><head><title>ESP8266 conf</title></head><body>";
const char* const DOC_FOOTER ="</body></html>";
const char* const DOC_REBOOTING ="<div>Saved! Rebooting...</div>";
const char* const DOC_FORM = "<h1>Wi-Fi conf</h1><form action='/' method='POST'> \
<div>SSID:</div> \
<div><input type='text' name='ssid' value='$SSID'/></div> \
<div>Password:</div> \
<div><input type='password' name='password' value='$PASSWORD'/></div> \
<div>Webserver user:</div> \
<div><input type='text' name='ws_user' value='$WSUSER'/></div> \
<div>Webserver password:</div> \
<div><input type='password' name='ws_password' value='$WSPASSORD'/></div> \
<div>OTA password:</div> \
<div><input type='password' name='ota_password' value='$OTAPASSORD'/></div> \
<div><input type='submit' value='Save'/></div></form>";



// Configuration for fallback access point
// if Wi-Fi connection fails.
const char* const AP_ssid = "ESP8266_fallback_AP";
const char* const AP_password = "SuperSecretPassword";


const char* const STATE_OK_RESP = "{\"state\": \"ok\"}";
const char* const STATE_CHANGED_DOOR_RESP = "{ \"door\" : { \"status\": \"changed\" } } ";;
const char* const STATE_CHANGED_GATE_RESP = "{ \"gate\" : { \"status\": \"changed\" } } ";

#endif //__CONSTANTS_H_