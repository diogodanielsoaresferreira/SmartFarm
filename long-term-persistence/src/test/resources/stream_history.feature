Feature: Access the data sent from the sensor

        Scenario: Access sensor history
        Given a sensor 996
        When the sensor sends the values value1: {"value": "-1", "timestamp": "2018-05-06T22:54:00.839222", "status": "dead"}, value2: {"value": "15", "timestamp": "2018-05-05T22:54:00.839222", "status": "ok"}, value3: {"value": "0", "timestamp": "2018-05-05T22:55:00.839222", "status": "ok"} to the platform
        Then the value1: {"value": "-1", "timestamp": "2018-05-06T22:54:00.839222", "status": "dead"}, value2: {"value": "15", "timestamp": "2018-05-05T22:54:00.839222", "status": "ok"}, value3: {"value": "0", "timestamp": "2018-05-05T22:55:00.839222", "status": "ok"} are available on the platform
