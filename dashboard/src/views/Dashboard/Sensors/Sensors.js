import React, {Component} from 'react';

import {
    Row,
    Col,
    Card,
    CardBody,
    CardTitle,
    Table,
} from 'reactstrap';

class Sensors extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            sensors : [],
        };

        this.loadSensors();
    }

    componentDidMount() {
        this.checkAuthentication();
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

    loadSensors() {
        fetch('http://deti-engsoft-07.ua.pt:8000/sensors?access_token=' + localStorage.getItem('token')).then(results => {
            return results.json();
        }).then(values => {
            this.setState({sensors: values['_embedded']['sensors']});
        });
    }

    deleteSensor(sensorId){
        let xhr = new XMLHttpRequest();
        xhr.open("DELETE", 'http://deti-engsoft-07.ua.pt:8000/sensors/' + sensorId + '?access_token=' + localStorage.getItem('token'), true);
        xhr.setRequestHeader("Content-type", "application/json");
        xhr.onload = function () {
            if(this.status > 200 && this.status < 300){
                location.reload();
            }
        };
        xhr.send();
    }

    static getId(sensor) {
        let sensorLink = sensor.split('/');
        return sensorLink[sensorLink.length - 1];
    }

    render() {

        return (
            <div className="animated fadeIn">
                <Row>
                    <Col>
                        <Card>
                            <CardBody>
                                <Row>
                                    <Col sm="5">
                                        <CardTitle className="mb-0">Sensors</CardTitle>
                                        <div className="small text-muted">March 2018</div>
                                    </Col>
                                </Row>
                                <br/>
                                <Table bordered>
                                    <thead>
                                    <tr>
                                        <th>Sensor ID</th>
                                        <th>Name</th>
                                        <th>Type</th>
                                        <th>Model</th>
                                        <th>Actuator</th>
                                        <th>Farm ID</th>
                                        <th>Status</th>
                                        <th>Delete</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    {
                                        this.state.sensors.map((object, key) =>
                                            <tr key={key}>
                                                <th scope="row"><a href={"/#/sensors/" + Sensors.getId(object._links.self.href)}>
                                                    {Sensors.getId(object._links.self.href)}</a></th>
                                                <td>{object.name}</td>
                                                <td>{object.type}</td>
                                                <td>{object.model}</td>
                                                <td>{String(object.actuator)}</td>
                                                <td><a href={"/#/farms/" + object.farm}>{String(object.farm)}</a></td>
                                                <td>{object.status ? object.status : '--'}</td>
                                                <td><button onClick={this.deleteSensor.bind(this, object._links.self.href.split('sensors/')[1])}>Delete</button></td>
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

export default Sensors;
