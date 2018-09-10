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
PERIOD = 0

'''
	Event that manages a flag that can be set true or false
	It is used to finish each thread if the user wants to exit the program
'''
e = Event()

'''
	Kafka URL to POST
'''
KAFKA_URL = "127.0.0.1:9092"
SENSOR_ID = input("Enter the device ID: ")
p = Producer({'bootstrap.servers': KAFKA_URL})

'''
	Post a tuple <key, value> to kafka
'''
def send_to_kafka(key, value):
	p.produce("sensor_data", value.encode('utf-8'), key)
	p.flush()

'''
	Read value from keyboard
'''
def read_value():
	while(not e.isSet()):
		value = input("Enter a value: ")
		state = input("Enter the sensor state: ")
		try:
			payload = {'value': value, 'timestamp': datetime.datetime.now().isoformat(), 'status': state}
			send_to_kafka(SENSOR_ID, json.dumps(payload))
			print("Value: %s sent." % (value))
		except Exception as exc:
			print("Error publishing the value: %s" % (exc))
		if(PERIOD != 0):
			print("Wait %d seconds..." % PERIOD)
			sleep(PERIOD)


'''
	Main function
	Program is initialized with the thread launching, waiting for the user to exit the program.
'''
def main():
	print('Enter values to POST. Press CTRL-C to interrupt...')

	thread_value = Thread(target = read_value)
	thread_value.start()

	while(thread_value.isAlive()):
		try: 
			sleep(1)
		except KeyboardInterrupt:
			e.set()
			
			print('Exiting...')
			thread_value.join()


if __name__ == '__main__':
	main()
