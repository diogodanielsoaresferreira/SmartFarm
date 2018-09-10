Feature: Visualize farm sensors’ location on a map

	Scenario Outline: New sensor
	Given a new sensor with latitude <latitude> and longitude <longitude>
	When the user checks the sensor description
	Then the sensor latitude and longitude should be <latitude> and <longitude>, respectively

	Examples:
		| latitude | longitude |
		|  40.0    |  -8.0     |
		|  35.1    |  3.7	   |
		|  -20.5   |  20.5     |