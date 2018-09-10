Feature: Visualize farm sensors’ average values
    
    Scenario Outline: Read the average value from a sensor
    Given a new sensor <id>
    When the sensor sends values value1: <data1>, value2: <data2>, value3: <data3> to the platform
    Then the average value of the sensor should be <average>

    Examples:
        | id  | data1                                                                       | data2                                                                         | data3                                                                      | average  |
        | 989 | {"value": "14", "timestamp": "2018-05-05T22:54:00.839222", "status": "ok"}  | {"value": "15", "timestamp": "2018-05-05T22:54:00.839222", "status": "ok"}    | {"value": "16", "timestamp": "2018-05-05T22:54:00.839222", "status": "ok"} | 15.0     |
        | 988 | {"value": "0", "timestamp": "2018-05-05T22:54:00.839222", "status": "ok"}   | {"value": "5", "timestamp": "2018-05-05T22:54:00.839222", "status": "ok"}     | {"value": "10", "timestamp": "2018-05-05T22:54:00.839222", "status": "ok"} | 5.0      |
        | 987 | {"value": "10", "timestamp": "2018-05-05T22:54:00.839222", "status": "ok"}  | {"value": "20", "timestamp": "2018-05-05T22:54:00.839222", "status": "ok"}    | {"value": "30", "timestamp": "2018-05-05T22:54:00.839222", "status": "ok"} | 20.0     |

