import React, {Component} from 'react';
import SockJS from 'sockjs-client';
import Stomp from '@stomp/stompjs';

import {
    Row,
    Col,
    Card,
    CardBody,
    CardTitle,
    Table,
} from 'reactstrap';

class Alert extends React.Component {
    constructor(props) {
        super(props);
        
        this.alertId = this.props.match.params.number;

        this.state = {
            sensors: [],
            actuators: [],
            triggeredAlerts: []
        };
    }

    componentDidMount() {
        this.checkAuthentication();
        this.loadAlertData();
        this.loadTriggeredAlerts();
        this.loadActuators();
        this.socketConnect();
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

    loadAlertData() {
        fetch('http://deti-engsoft-07.ua.pt:8000/alerts/' + this.alertId, {headers: {"Access-Control-Allow-Origin": "*", "Access-Control-Allow-Headers": "content-type, Authorization", "Access-Control-Allow-Methods": "GET,HEAD,POST,PUT,PATCH,DELETE,OPTIONS,TRACE"}
        }).then(results => {
            return results.json();
        }).then(values => {
            this.setState({
                type: Alert.getAlertName(values.alertType),
                threshold: values.threshold,
                sensors: values.sensors,
            });
        });
    }

    loadTriggeredAlerts() {
        fetch('http://deti-engsoft-07.ua.pt:8000/triggered-alerts/', {headers: {"Access-Control-Allow-Origin": "*", "Access-Control-Allow-Headers": "content-type, Authorization", "Access-Control-Allow-Methods": "GET,HEAD,POST,PUT,PATCH,DELETE,OPTIONS,TRACE"}
        }).then(results => {
            return results.json();
        }).then(values => {
            let self = this;
            
            let triggeredAlerts = values['_embedded']['triggeredalerts'];
            let alertTriggeredAlerts = triggeredAlerts.filter(function(alert) {
                return alert.alertId == self.alertId;
            });
            
            this.setState({triggeredAlerts: alertTriggeredAlerts});
        });

    }

    loadActuators() {
        fetch('http://deti-engsoft-07.ua.pt:8000/alerts/actuators/', {headers: {"Access-Control-Allow-Origin": "*", "Access-Control-Allow-Headers": "content-type, Authorization", "Access-Control-Allow-Methods": "GET,HEAD,POST,PUT,PATCH,DELETE,OPTIONS,TRACE"}
        }).then(results => {
            return results.json();
        }).then(values => {
            let self = this;
            
            let actuators = values['_embedded']['actuators'];
            let alertActuatorsList = actuators.filter(function(actuator) {
                return actuator.alertId == self.alertId;
            });
            let alertActuators = [];
            alertActuatorsList.forEach(function(actuator) {
                alertActuators.push(actuator.sensors);
            });

            this.setState({actuators: alertActuators});
        });
    }

    socketConnect() {
        // connect to smartfarm websocket
        let socket = new SockJS('http://deti-engsoft-07.ua.pt:8000/triggered-alerts/smartfarm');
        let ws = Stomp.over(socket);

        // disable debug logs
        ws.debug = null;

        let self = this;
        ws.connect({}, function () {
            ws.subscribe("/topic/alerts", function (alert) {
                alert = JSON.parse(alert.body);
                if (alert.alertId == self.alertId) {
                    // add triggered alarm to the list
                    self.state.triggeredAlerts.push({
                        'alertId': alert.alertId,
                        'valueSensor': alert.value,
                        'dateCreated': alert.dateCreated
                    });
                    self.forceUpdate();
                }
            });
        }, function (error) {
            console.log(error);
        });
    }


    static getAlertName(alertType) {
        let alertNames = {
            'min': 'Minimum',
            'max': 'Maximum',
            'averageMin': 'Minimum Average',
            'averageMax': 'Maximum Average'
        }

        return alertNames[alertType];
    }

    render() {
        return (
            <div className="animated fadeIn">
                <Row>
                    <Col>
                        <Card>
                            <CardBody>
                                <p><b>Alert Type</b>: {this.state.type}</p>
                                <p><b>Threshold</b>: {this.state.threshold}</p>
                                <p><b>Sensors</b>:</p>
                                { this.state.sensors.length > 0 ? (
                                    this.state.sensors.map((sensor, key) =>
                                        <span key={key} >
                                            <a href={"/#/sensors/" + sensor}>{sensor}</a>
                                            <br/>
                                        </span>
                                    )):
                                    (
                                        <p>No sensors configured</p>
                                    )
                                }
                                <br/>
                                <p><b>Actuators</b>:</p>
                                { this.state.actuators.length > 0 ? (
                                    this.state.actuators .map((actuator, key) =>
                                        <span key={key} >
                                            <a href={"/#/sensors/" + actuator}>{actuator}</a>
                                            <br/>
                                        </span>
                                    )):
                                    (
                                        <p>No actuators configured</p>
                                    )
                                }
                            </CardBody>
                        </Card>
                        <Card>
                            <CardBody>
                                <Row>
                                    <Col sm="5">
                                        <CardTitle className="mb-0">Triggered Alerts</CardTitle>
                                    </Col>
                                </Row>
                                <br/>
                                <Table bordered>
                                    <thead>
                                    <tr>
                                        <th>Triggered Value</th>
                                        <th>Timestamp</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    {
                                        this.state.triggeredAlerts.map((alert, key) =>
                                            <tr key={key}>
                                                <td>{alert.valueSensor}</td>
                                                <td>{(new Date(alert.dateCreated)).toLocaleString()}</td>
                                            </tr>
                                        )
                                    }
                                    </tbody>
                                </Table>
                            </CardBody>
                        </Card>
                    </Col>
                </Row>
            </div>
        )
    }
}

export default Alert;
