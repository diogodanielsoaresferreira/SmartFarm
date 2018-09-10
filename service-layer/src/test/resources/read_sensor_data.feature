Feature: Access the data sent from the sensor
	
	Scenario Outline: Read last value sent from sensor
	Given a sensor <id>
	When the sensor sends the data <data> to the platform
	Then the <data> is available on the platform

	Examples:
		| id 	 | data 																	    |
		| 999	 | {'value': '15', 'timestamp': '2018-05-05T22:54:00.839222', 'status': 'ok'}   |
		| 998	 | {'value': '16.5', 'timestamp': '2018-05-06T22:54:00.839222', 'status': 'ok'} |
		| 997	 | {'value': '14', 'timestamp': '2018-05-06T22:54:00.839222', 'status': 'dead'} |
		| 999	 | {'value': '17.5', 'timestamp': '2018-05-06T22:54:00.839222', 'status': 'ok'} |

        Scenario: Access sensor history
        Given a sensor 996
        When the sensor sends the values to the platform: value1: {'value': '-1', 'timestamp': '2018-05-06T22:54:00.839222', 'status': 'dead'}, value2: {'value': '15', 'timestamp': '2018-05-05T22:54:00.839222', 'status': 'ok'}, value3: {'value': '0', 'timestamp': '2018-05-05T22:55:00.839222', 'status': 'ok'}
        Then the value1: {'value': '-1', 'timestamp': '2018-05-06T22:54:00.839222', 'status': 'dead'}, value2: {'value': '15', 'timestamp': '2018-05-05T22:54:00.839222', 'status': 'ok'}, value3: {'value': '0', 'timestamp': '2018-05-05T22:55:00.839222', 'status': 'ok'} are available on the platform
