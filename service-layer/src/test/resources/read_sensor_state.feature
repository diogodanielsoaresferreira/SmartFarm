Feature: Visualize farm sensors’ state

    Scenario: Read sensors' state
    Given a new sensor with state ok
    When the sensor sends a message {"value": "15", "timestamp": "2018-05-05T22:54:00.839222", "status": "broken"} to the platform
    Then the sensor state should be broken

