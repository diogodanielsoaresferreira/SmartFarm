Feature: Trigger alerts

	Scenario Outline: Sensor sends a value that triggers an alert
	Given an alert of type <type> and threshold <threshold> for the sensor <id>
	When a sensor sends to the platform <data>
	Then an alert is triggered

	Examples:
		| id | type | data | threshold |
		| 500 | min | {"value": "14", "timestamp": "2018-05-05T22:54:00.839222", "status": "ok"} | 500 |
		| 501 | max | {"value": "14", "timestamp": "2018-05-05T22:54:00.839222", "status": "ok"} | 1 |
		| 502 | averageMax | {"value": "14", "timestamp": "2018-05-05T22:54:00.839222", "status": "ok"} | -50 |
		| 503 | averageMin | {"value": "14", "timestamp": "2018-05-05T22:54:00.839222", "status": "ok"} | -50 |

