# smart-home-remote
Smart Home Remote Control

This project tries to facilitate the remote control of some IOT resources deployed localy on my home network.

### Description

The story behind this project is based on several broken Roger H80/TX22 remotes that layed on my cabinet that used to control the front gate or garage door.
In many cases I ended up buying new remotes or clone of them due to the fact that plastic shell of previous remotes broked or the battery connection was not stable.

For this reason I decided to dismantle one of the original remotes and link their buttons to some handcrafted circuits that act as basic switches.

First approach was to use a RasberryPI as a webserver that coordinates the GPIO pins, linked via some octocuplers to remotes buttons. 
Soon I have realized that this keeps my RPI 4 linked to this project and I can not use it anymore to another one, so I decided to replaces with something cheaper component, and this ended up in the V2 project where I have used an ESP-12F as web server and GPIO controler.


## Components

### Hardware

#### V1
- RaspberryPI (4) (RPI)
- 220 Ohm resistor - 2 pieces
- 10k Ohm resistor - 2 pieces
- PC817 Octocupler  - 2 pieces
- 2N2222A Transistor - 2 peces
- 433 MHz Generic Remote control (2 buttons)

#### V2
- ESP-12F (ESP8266)
- 220 Ohm resistor - 2 pieces
- 100 Ohm resistor - 2 pieces
- PC817 Octocupler  - 2 pieces
- 2N2222A Transistor - 2 peces
- 433 MHz Generic Remote control (2 buttons)
- YwRobot Breadboard Power Supply
- 12 V power supply

![Images for final boards](./pictures/README.md)

Note: schematics for this can be found on [./hardware_schematics/SmartHouseRemote.fzz](./hardware_schematics/SmartHouseRemote.fzz) . The file can be opened using Fritzing Software. 
```bash
sudo apt install fritzing
``` 



### Software
#### V1
- Nodered HTTP service that receives requests from external devices (phone app, web pages) and translates them in GPIO commands for RPI. Check [ ./server/nodered/flows.json ](./server/nodered/flows.json)
- Android phone application preprovisioned with authentication credentials. Check [./client/android/SmartHouseRemote](./client/android/SmartHouseRemote)
#### V2
- ESP8266WebServer that receives requests from external devices (phone app, web pages) and translates them in GPIO commands. Check [ ./server/ESPServer/src/main.cpp ](./server/ESPServer/src/main.cpp)
- Android phone application preprovisioned with authentication credentials. Check [./client/android/SmartHouseRemote](./client/android/SmartHouseRemote)

 ### Use case:
Phone application will send HTTP requests to web service to manage warious hardware devices (first scenario is to control the 433 Mhz Roger remote control)


### Software Used
#### V1 - RaspberryPI
- Raspberry Pi OS Lite - https://www.raspberrypi.com/software/operating-systems/
- Nodered for RPI - see https://nodered.org/docs/getting-started/raspberrypi

Changes made to default Raspbian OS
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

#### V2
- ESP-12F - ESP8266 Arduino Firmware
- Libraries
    - ESP8266WebServer
    - ArduinoOTA
    - ESP8266WiFi

Credits:

- Gaven MacDonald - for its tutorial about octocuplers and transistors https://youtu.be/pYENAGK8qH4
- Indrek Luuk - for ESP wiring and programming tutorials https://circuitjournal.com/esp8266-with-arduino-ide
- Andreas Spiess for its tutorial on power supplies https://youtu.be/DLQ1E5pDcBU
- https://fritzing.org - for their PCB editor