#!/usr/bin/env python

'''
	Simple aggregator for various sensor simulators
'''

from threading import Thread, Event
from time import sleep
import random
import datetime
import json
from confluent_kafka import Producer

'''
	Simulators period
'''
SIM_TEMPERATURE_PERIOD = 5
SIM_HUMIDY_PERIOD = 5
SIM_UVINDEX_PERIOD = 5

'''
	Event that manages a flag that can be set true or false
	It is used to finish each thread if the user wants to exit the program
'''
e = Event()

'''
	Kafka URL to POST
'''
KAFKA_URL = "deti-engsoft-01.ua.pt:9092"
SENSOR_ID_TEMP = "106"
SENSOR_ID_HUM = "1"
SENSOR_ID_UV = "2"
p = Producer({'bootstrap.servers': KAFKA_URL})

'''
	Post a tuple <key, value> to kafka
'''


def send_to_kafka(key, value):
    p.produce("sensor_data", value.encode('utf-8'), key)
    p.flush()


'''
	Read temperature simulator
'''


def read_temperature_simulator():
    mean = 20
    variation = 4
    while (not e.isSet()):
        value = str(round((mean + random.uniform(-variation, variation)), 2))
        try:
            payload = {'value': value, 'timestamp': datetime.datetime.now().isoformat(), 'status': "OK"}
            send_to_kafka(SENSOR_ID_TEMP, json.dumps(payload))
            print("Value: %s sent." % (value))
        except:
            print("Error publishing temperature simulator values")
        sleep(SIM_TEMPERATURE_PERIOD)


'''
	Read humidity simulator
'''


def read_humidity_simulator():
    mean = 90
    variation = 5
    while (not e.isSet()):
        value = str(int(mean + random.uniform(-variation, variation)))
        try:
            # payload = {'value': value, 'timestamp': datetime.datetime.now().isoformat(), 'status': "OK"}
            # send_to_kafka(SENSOR_ID_HUM, json.dumps(payload))
            print("Humidity: %s sent." % value)
        except:
            print("Error publishing humidity simulator values")
        sleep(SIM_HUMIDY_PERIOD)


'''
	Read UV index simulator
'''


def read_uvindex_simulator():
    mean = 5
    variation = 1
    while (not e.isSet()):
        value = str(int(mean + random.uniform(-variation, variation)))
        try:
            # payload = {'value': value, 'timestamp': datetime.datetime.now().isoformat(), 'status': "OK"}
            # send_to_kafka(SENSOR_ID_UV, json.dumps(payload))
            print("UV Index: %s sent." % value)
        except:
            print("Error publishing UV index simulator values")
        sleep(SIM_UVINDEX_PERIOD)


'''
	Main function
	Program is initialized with the thread launching, waiting for the user to exit the program.
'''


def main():
    print('Simulators running. Press CTRL-C to interrupt...')

    thread_temperature = Thread(target=read_temperature_simulator)
    thread_temperature.start()

    thread_humidity = Thread(target=read_humidity_simulator)
    thread_humidity.start()

    thread_uvindex = Thread(target=read_uvindex_simulator)
    thread_uvindex.start()

    while (thread_temperature.isAlive() or thread_humidity.isAlive() or thread_uvindex.isAlive()):
        try:
            sleep(1)
        except KeyboardInterrupt:
            e.set()

            print('Exiting...')

            thread_temperature.join()
            thread_humidity.join()
            thread_uvindex.join()


if __name__ == '__main__':
    main()
