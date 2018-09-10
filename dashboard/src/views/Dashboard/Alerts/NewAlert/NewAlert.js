import React, {Component} from 'react';

import {
    Row,
    Col,
    Card,
    CardBody,
    Button,
    Label,
    Form,
    FormGroup,
    Input
} from 'reactstrap';

class NewAlert extends React.Component {
    constructor(props) {
        super(props);

        this.handleChange = this.handleChange.bind(this);
        this.handleSelectSensorChange = this.handleSelectSensorChange.bind(this);
        this.handleSelectActuatorChange = this.handleSelectActuatorChange.bind(this);
        this.addFormSensor = this.addFormSensor.bind(this);
        this.addFormActuator = this.addFormActuator.bind(this);
        this.removeFormSensor = this.removeFormSensor.bind(this);
        this.removeFormActuator = this.removeFormActuator.bind(this);
        this.addNewAlert = this.addNewAlert.bind(this);

        this.state = {
            sensorsNumber: 1,
            actuatorsNumber: 0,
            sensors: [],
            sensorActuators: [],
            actuatorIds: [],
            sensorIds: [],
            alertType: 'Maximum',
            threshold: 0,
            valueToBeSent: 'Triggered'
        };

    }

    componentDidMount() {
        this.checkAuthentication();
        this.loadSensors();
    }

    checkAuthentication() {
        let xhr = new XMLHttpRequest();
        xhr.open("GET", 'http://deti-engsoft-07.ua.pt:8000/oauth/check_token?token=' + localStorage.getItem('token'), true);
        xhr.setRequestHeader("Content-type", "application/json");
        xhr.setRequestHeader("Authorization", "Basic c21hcnRmYXJtOnNtYXJ0ZmFybQ==");
        xhr.onload = function () {
            if (this.status < 200 || this.status >= 300) {
                document.location.href = "/#/login";
            }
        };
        xhr.send();
    }

    handleChange(event) {
        this.setState({[event.target.name]: event.target.value});
    }

    handleSelectSensorChange(event) {
        let sensor = this.state.sensors.filter(function(sensor) {
            return sensor.name === event.target.value;
        })[0];
        let sensorLink = sensor._links.self.href.split('/');

        this.state.sensorIds[event.target.id] = sensorLink[sensorLink.length - 1];
        this.forceUpdate();
    }

    handleSelectActuatorChange(event) {
        let sensor = this.state.sensors.filter(function(sensor) {
            return sensor.name === event.target.value;
        })[0];
        let sensorLink = sensor._links.self.href.split('/');

        this.state.actuatorIds[event.target.id] = sensorLink[sensorLink.length - 1];
        this.forceUpdate();
    }

    addFormSensor(event) {
        if(this.state.sensorsNumber == this.state.sensors.length)
            return;

        this.state.sensorsNumber++;
        
        // update state with first deviceId
        let sensorLink = this.state.sensors[0]._links.self.href.split('/');
        this.state.sensorIds.push(sensorLink[sensorLink.length - 1]);
        
        this.forceUpdate();
    }

    removeFormSensor(event) {
        this.state.sensorsNumber--;
        
        // remove last item from list
        this.state.sensorIds.pop();
        
        this.forceUpdate();
    }

    addFormActuator(event) {
        if(this.state.actuatorsNumber == this.state.sensorActuators.length)
            return;

        this.state.actuatorsNumber++;
        
        // update state with first deviceId
        let sensorLink = this.state.sensorActuators[0]._links.self.href.split('/');
        this.state.actuatorIds.push(sensorLink[sensorLink.length - 1]);
        
        this.forceUpdate();
    }

    removeFormActuator(event) {
        this.state.actuatorsNumber--;
        
        // remove last item from list
        this.state.actuatorIds.pop();
        
        this.forceUpdate();
    }

    addNewAlert(event) {
        event.preventDefault();

        this.addOnlyAlert();
    }

    addOnlyAlert() {
        let self = this;
        let xhr = new XMLHttpRequest();

        xhr.open("POST", 'http://deti-engsoft-07.ua.pt:8000/alerts/', true);
        xhr.setRequestHeader("Content-type", "application/json");
        xhr.setRequestHeader("Access-Control-Allow-Origin", "*");
        xhr.setRequestHeader("Access-Control-Allow-Headers", "content-type");
        xhr.setRequestHeader("Access-Control-Allow-Methods", "GET,HEAD,POST,PUT,PATCH,DELETE,OPTIONS,TRACE");
        xhr.onload = function () {
            if(this.status > 200 && this.status < 300) {
                let alertId = NewAlert.getId(JSON.parse(this.response)._links.self.href);
                self.addActuatorsToAlert(alertId);
            }
        };
        xhr.send(
            JSON.stringify({
                alertType: NewAlert.getAlertType(this.state.alertType),
                threshold: this.state.threshold,
                sensors: this.state.sensorIds,
            })
        );
    }

    addActuatorsToAlert(alertId) {
        let xhr = new XMLHttpRequest();
        xhr.open("POST", 'http://deti-engsoft-07.ua.pt:8000/alerts/actuators/', true);
        xhr.setRequestHeader("Content-type", "application/json");
        xhr.setRequestHeader("Access-Control-Allow-Origin", "*");
        xhr.setRequestHeader("Access-Control-Allow-Headers", "content-type");
        xhr.setRequestHeader("Access-Control-Allow-Methods", "GET,HEAD,POST,PUT,PATCH,DELETE,OPTIONS,TRACE");
        xhr.onload = function () {
            if(this.status > 200 && this.status < 300){
                document.location.href = "/#/alerts";
            }
        };

        xhr.send(
            JSON.stringify({
                alertId: alertId,
                valueToBeSent: this.state.valueToBeSent,
                sensors: this.state.actuatorIds,
            })
        );
    }
    
    loadSensors() {
        fetch('http://deti-engsoft-07.ua.pt:8000/sensors?access_token=' + localStorage.getItem('token')).then(results => {
            return results.json();
        }).then(values => {
            this.setState({sensors: values['_embedded']['sensors']});

            let actuators = this.state.sensors.filter(function(alert) {
                return alert.actuator;
            });

            // add sensors that are actuators and update state with first deviceId
            let sensorLink = this.state.sensors[0]._links.self.href.split('/');
            this.setState({
                sensorActuators: actuators,
                sensorIds: [sensorLink[sensorLink.length - 1]]
            });
        })
    }

    static getAlertType(alertName) {
        let alertTypes = {
            'Minimum': 'min',
            'Maximum': 'max',
            'Minimum Average': 'averageMin',
            'Maximum Average': 'averageMax'
        }

        return alertTypes[alertName];
    }

    static getId(alert) {
        let alertLink = alert.split('/');
        return alertLink[alertLink.length - 1];
    }

    render() {
        let sensors = []
        for (var i = 0; i < this.state.sensorsNumber; i++) {
            sensors.push(
                <Input style={{"marginTop": 5 + "px"}} key={i} id={i} type="select" onChange={this.handleSelectSensorChange}>
                    { this.state.sensors.map((sensor, key) =>
                            <option key={key}>{sensor.name}</option>
                        )
                    }
                </Input>
            );
        }

        let actuators = []
        for (var i = 0; i < this.state.actuatorsNumber; i++) {
            actuators.push(
                <div key={i} id={i} >
                    <Label for="exampleSelect" style={{"marginTop": 5 + "px"}}>Sensor</Label>
                    <Input style={{"marginTop": 5 + "px"}} type="select" onChange={this.handleSelectActuatorChange}>
                        { this.state.sensorActuators.map((sensor, key) =>
                                <option key={key}>{sensor.name}</option>
                            )
                        }
                    </Input>
                    <Label for="exampleSelect" style={{"marginTop": 5 + "px"}}>Value to send to the actuators</Label>
                    <Input name="valueToBeSent" style={{"marginTop": 5 + "px"}} onChange={this.handleChange}/>
                </div>
            );
        }

        return (
            <div className="animated fadeIn">
                <Row>
                <Col>
                    <Card>
                    <CardBody>
                        <Form onSubmit={this.addNewAlert}>
                            <FormGroup>
                                <Label for="exampleSelect">Sensor</Label>
                                { sensors }
                            </FormGroup>
                            <Button color="primary" size="sm" onClick={this.addFormSensor}>Add sensor</Button>  
                            { this.state.sensorsNumber > 1 &&
                                <Button style={{"marginLeft": 5 + "px"}} color="primary" size="sm" onClick={this.removeFormSensor}>Remove sensor</Button>
                            }
                            <br/>
                            <br/>
                            <FormGroup>
                                <Label for="exampleSelect">Alert type</Label>
                                <Input type="select" name="alertType" onChange={this.handleChange}>
                                    <option>Maximum</option>
                                    <option>Minimum</option>
                                    <option>Maximum Average</option>
                                    <option>Minimum Average</option>
                                </Input>
                            </FormGroup>
                            <FormGroup>
                                <Label for="exampleNumber">Threshold</Label>
                                <Input type="number" name="threshold" onChange={this.handleChange}/>

                            </FormGroup>
                            <FormGroup>
                                <Label for="exampleSelect">Actuators</Label>
                                { actuators }
                                
                            </FormGroup>
                            <Button color="primary" size="sm" onClick={this.addFormActuator}>Add actuator</Button>
                            { this.state.actuatorsNumber > 0 &&
                                <Button style={{"marginLeft": 5 + "px"}} color="primary" size="sm" onClick={this.removeFormActuator}>Remove Actuator</Button>
                            }
                            <br/>
                            <br/>
                            <Button>Add new alert</Button>
                        </Form>
                    </CardBody>
                    </Card>
                </Col>
                </Row>
            </div>
        )
    }
}

export default NewAlert;
