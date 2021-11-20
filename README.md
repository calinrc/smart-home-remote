# smart-home-remote
Smart Home Remote Control

This project tries to facilitate remote control to some IOT resources deployed localy on home netrok

## Components

### Hardware
- RaspberryPI (4) (RPI)
- 5V Relay board (this board already contains a )
- 433 MHz Roger Remote control 

### Software
 - HTTP service that receive requests from external devices (phone app, web pages) and translates them in GPIO commands for RPI
 - Android phone application preprovisioned with authentication token

 ### Use case:
Phone application will send HTTP requests to web service to manage warious hardware devices (first scenario is to control the 433 Mhz Roger remote control)


### Software Used
- Raspberry Pi OS Lite
- Python3 (default version - 3.7.3)

### Changes made to default OS
- Chnage default python from py2 to py3
     ```bash
     sudo rm /usr/bin/python
     sudo ln -s /usr/bin/python3 /usr/bin/python
     ls -l /usr/bin/python
     ```
- Install pip
    ```bash
    sudo apt-get install python3-pip
    ```
- Make pip3 default pip command
    ```bash
    sudo ln -s /usr/bin/pip3 /usr/bin/pip
    ```
- Create python virtual environment
  ```bash
  sudo apt-get install python3-venv
  mkdir ~/APPS
  python -m venv ~/APPS/venv
  export PATH="/home/pi/APPS/venv/bin:$PATH"
  pip install RPi.GPIO
  ```