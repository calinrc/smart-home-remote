# smart-home-remote
Smart Home Remote Control

This project tries to facilitate remote control to some IOT resources deployed localy on home network

## Components

## V1
### Hardware
- RaspberryPI (4) (RPI)
- 220 Ohm resistor - 2 pieces
- 10k Ohm resistor - 2 pieces
- PC817 Octocupler  - 2 pieces
- 2N2222A Transistor - 2 peces
- 433 MHz Generic Remote control (2 buttons)


## V2
### Hardware
- ESP-12F (ESP8266)
- 220 Ohm resistor - 2 pieces TBD
- 10k Ohm resistor - 2 pieces
- PC817 Octocupler  - 2 pieces
- 2N2222A Transistor - 2 peces
- 433 MHz Generic Remote control (2 buttons)


Note: schematics for this can be found on [./hardware_schematics/SmartHouseRemote.fzz](./hardware_schematics/SmartHouseRemote.fzz) . The file can be opened using Fritzing Software. 
```bash
sudo apt install fritzing
``` 



### Software
#### V1
- Nodered HTTP service that receives requests from external devices (phone app, web pages) and translates them in GPIO commands for RPI. Check [ ./server/nodered/flows.json ](./server/nodered/flows.json)
#### V2
- ESP8266WebServer that receives requests from external devices (phone app, web pages) and translates them in GPIO commands. Check [ ./server/ESPServer/src/main.cpp ](./server/ESPServer/src/main.cpp)


- Android phone application preprovisioned with authentication credentials. Check [./client/android/SmartHouseRemote](./client/android/SmartHouseRemote)

 ### Use case:
Phone application will send HTTP requests to web service to manage warious hardware devices (first scenario is to control the 433 Mhz Roger remote control)


### Software Used
- Raspberry Pi OS Lite - https://www.raspberrypi.com/software/operating-systems/
- Nodered for RPI - see https://nodered.org/docs/getting-started/raspberrypi

### Changes made to default Raspbian OS
- Install nodered on
     ```bash
     sudo apt update
     sudo apt install nodered
     ```
- Install nodered as service
    ```bash
    sudo systemctl enable nodered.service
    ```
- generate credentials for admin and default user
    ```bash
    node-red admin hash-pw
    ```
- Update nodered admin and user credentials
   - update `~/.node-red/settings.js` with generated password on following places
    ```json

    adminAuth: {
        type: "credentials",
        users: [{
            username: "admin",
            password: "generated_passwd_here",
            permissions: "*"
        }]
    },    

    ...

    httpNodeAuth: {user:"some_user_here",pass:"generated_passwd_here"},

    ```

  Credits:

  - Gaven MacDonald - for its great tutorial about octocuplers and transistors https://youtu.be/pYENAGK8qH4
  - Indrek Luuk - for great ESP wiring and programming tutorials https://circuitjournal.com/esp8266-with-arduino-ide
  - https://fritzing.org - for their great PCB editor