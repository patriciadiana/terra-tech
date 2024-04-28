import os
import sys
import glob
import time
import board
import subprocess
import numpy as np
import adafruit_dht
import tensorflow as tf
import RPi.GPIO as GPIO
from keras.preprocessing import image
from keras.src.legacy.preprocessing.image import ImageDataGenerator

channel = 21

GPIO.setmode(GPIO.BCM)
GPIO.setwarnings(False)
GPIO.setup(channel, GPIO.IN)

dhtDevice = adafruit_dht.DHT11(board.D23, use_pulseio=False)

no_data = 5
sum_temp = 0
sum_humidity = 0
valid_readings = 0

fswebcam_command = ['fswebcam', "input/test.jpg"]
subprocess.run(fswebcam_command, check=True)
time.sleep(1)

if GPIO.input(channel) == GPIO.LOW:
    soil_status = "Wet"
else:
    soil_status = "Dry"

while(valid_readings < no_data):
    try:
        temperature = dhtDevice.temperature
        humidity = dhtDevice.humidity
        if temperature is not None and humidity is not None:
            valid_readings += 1
            sum_temp += temperature
            sum_humidity += humidity
            time.sleep(1.0)
            
    except RuntimeError as error:
        print(error.args[0])
        time.sleep(1.0)
        continue
    except Exception as error:
        dhtDevice.exit()
        raise error
        
avg_temp = sum_temp / no_data
avg_humidity = sum_humidity / no_data
        
flower_categories = ['Daisy', 'Dandelion' , 'Rose', 'Sunflower' , 'Tulip']

#load the saved model
model = tf.keras.models.load_model("flowers.h5")

img_dir = "input/"
data_path = os.path.join(img_dir,'*')
files = glob.glob(data_path)

num = 0 

f1 = files[0]

test_image = image.load_img(f1, target_size=(224,224))

#convert the image into array
test_image = image.img_to_array(test_image)

#expand the array with another dimenation
test_image = np.expand_dims(test_image, axis=0)

# predict the category of an image 
result = model.predict(test_image) 

indPositionMax = np.argmax(result[0])

flower_predict = flower_categories[indPositionMax]
text = flower_predict + " " + str(avg_temp) + " " + str(avg_humidity) + " " + soil_status

with open("output.txt", "w") as f:
    f.write(text)
    
sys.stderr.close()