import RPi.GPIO as GPIO
from gpiozero import LED
from time import sleep
import time
import sys

#GPIO SETUP
channel = 21
relay = 27

motor_status = False
no_of_pumps = int(sys.argv[1])
no_of_seconds = int(sys.argv[2])

red_led = LED(17)
green_led = LED(4)

GPIO.setmode(GPIO.BCM)
GPIO.setwarnings(False)

GPIO.setup(channel, GPIO.IN)
GPIO.setup(relay, GPIO.OUT)
GPIO.output(relay, GPIO.HIGH)

def motor_on(pin):
    GPIO.output(pin, GPIO.LOW)

def motor_off(pin):
    GPIO.output(pin, GPIO.HIGH)

red_led.off()
green_led.off()

while True:
    if GPIO.input(channel) == GPIO.LOW:
        green_led.on()
        red_led.off()
        print("wet soil, no water needed")
        if motor_status:
            motor_off(relay)
            sleep(1)
            motor_status = False
        else:
            motor_status = False
    else:
        red_led.on()
        green_led.off()
        print("dry soil, water needed")
        if motor_status == False:
            for _ in range(no_of_pumps):
                motor_on(relay)
                sleep(no_of_seconds)
                motor_off(relay)
                sleep(1)       
            with open("finish.txt", "w") as f:
                f.write("finish")
            motor_status = True
        else:
            motor_status = True
