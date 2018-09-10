Feature: Send automated value to an actuator

	Scenario Outline: Sensor sends a value that triggers an alert that has an actuator
	Given an alert defined with id of type <type> and threshold <threshold> for the sensor <id> with an actuator defined for the sensor <id2> with the value <value>
	When the first sensor sends data <data> to the platform
	Then the sensor <id2> will receive a message with the value <value>

	Examples:
		| id | id2 | type | data | threshold | value |
		| 500 | 504 | min | {"value": "14", "timestamp": "2018-05-05T22:54:00.839222", "status": "ok"} | 500 | 27 |
		| 501 | 505 | max | {"value": "14", "timestamp": "2018-05-05T22:54:00.839222", "status": "ok"} | 1 | 28 |
		| 502 | 506 | averageMax | {"value": "14", "timestamp": "2018-05-05T22:54:00.839222", "status": "ok"} | -50 | 30 |
		| 503 | 507 | averageMin | {"value": "14", "timestamp": "2018-05-05T22:54:00.839222", "status": "ok"} | -50 | 31 |

