Raspberry Pi:
1) Connect the PiCamera in the respective module.
2) Enable the camera
    $ sudo raspi-config
    Select Enable camera -> Enter -> Finish
3) mkdir PiCamera

PC:
1) scp camera.sh pi@raspberrypi:PiCamera/

RaspberryPi:
3) $ cd PiCamera
4) run ./camera.sh
Your photo will be saved in the PiCamera folder.

MISSING:

The sensor needs a control message to know when should it take a picture. To this, a solution that might be ok, is to implement a simple queue where the raspberrypi consumes the control messages and take and retrieve a photo IF needed. OR, in case of motion sensor, we may implement a simple script in the raspberry where it reads the values of the motion sensor, and depending on the conditions we configure, the PiCamera takes a picture - but will be harder because the alarms can be setted by the user, meaning the script would have to be rewritten everytime the user changes the threshould values.