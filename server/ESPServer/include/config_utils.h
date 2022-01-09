#ifndef __CONFIG_UTILS_
#define __CONFIG_UTILS_

#include <EEPROM.h>
#include "definitions.h"

// init EEPROM object
void initConf(WifiConf* wifiConf)
{
    EEPROM.begin(512);
}

void readWifiConf(WifiConf* wifiConf)
{
  // Read wifi conf from flash
  for (unsigned int i = 0; i < sizeof(WifiConf); i++)
  {
    ((char *)(wifiConf))[i] = char(EEPROM.read(i));
  }
  // Make sure that there is a 0
  // that terminatnes the c string
  // if memory is not initalized yet.
  wifiConf->cstr_terminator = 0;
}

void writeWifiConf(WifiConf* wifiConf)
{
  for (unsigned int i = 0; i < sizeof(WifiConf); i++)
  {
    EEPROM.write(i, ((char *)(wifiConf))[i]);
  }
  EEPROM.commit();
}

#endif //__CONFIG_UTILS_