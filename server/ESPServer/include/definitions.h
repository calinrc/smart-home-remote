#ifndef __DEFINITIONS_H_
#define __DEFINITIONS_H_

// Wi-Fi connection parameters.
// It will be read from the flash during setup.
struct WifiConf
{
  char wifi_ssid[50];
  char wifi_password[50];
  char ws_username[50];
  char ws_password[50];
  char ota_password[50];
  // Make sure that there is a 0
  // that terminatnes the c string
  // if memory is not initalized yet.
  char cstr_terminator = 0; // makse sure
};


#endif //__DEFINITIONS_H_