import React, {Component} from 'react';

import {
    Row,
    Col,
    Card,
    CardBody,
    CardTitle,
    Table,
} from 'reactstrap';

class Alerts extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            alerts: [],
        };

    }

    componentDidMount() {
        this.checkAuthentication();
        this.loadAlerts();
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

    loadAlerts() {
        fetch('http://deti-engsoft-07.ua.pt:8000/alerts/', {headers: {"Access-Control-Allow-Origin": "*", "Access-Control-Allow-Headers": "content-type, Authorization", "Access-Control-Allow-Methods": "GET,HEAD,POST,PUT,PATCH,DELETE,OPTIONS,TRACE"}
        }).then(results => {
            return results.json();
        }).then(values => {
            this.setState({alerts: values['_embedded']['alerts']});
        });
    }

    deleteAlert(alertUrl){
        let alertInfo = alertUrl.split('/');

        let xhr = new XMLHttpRequest();
        xhr.open("DELETE", 'http://deti-engsoft-07.ua.pt:8000/alerts/' + alertInfo[alertInfo.length-1], true);
        xhr.setRequestHeader("Content-type", "application/json");
        xhr.setRequestHeader("Access-Control-Allow-Origin", "*");
        xhr.setRequestHeader("Access-Control-Allow-Headers", "content-type");
        xhr.setRequestHeader("Access-Control-Allow-Methods", "GET,HEAD,POST,PUT,PATCH,DELETE,OPTIONS,TRACE");
        xhr.onload = function () {
            if(this.status > 200 && this.status < 300){
                location.reload();
            }
        };
        xhr.send();
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

    static getId(alert) {
        let alertLink = alert.split('/');
        return alertLink[alertLink.length - 1];
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
                                        <CardTitle className="mb-0">Configured Alerts</CardTitle>
                                    </Col>
                                </Row>
                                <br/>
                                <Table bordered>
                                    <thead>
                                    <tr>
                                        <th>Alert ID</th>
                                        <th>Alert Type</th>
                                        <th>Threshold Value</th>
                                        <th>Delete</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    {
                                        this.state.alerts.map((alert, key) =>
                                            <tr key={key}>
                                                <td>
                                                    <a href={"/#/alerts/" + Alerts.getId(alert._links.self.href)}>
                                                    {Alerts.getId(alert._links.self.href)}</a>
                                                </td>
                                                <td>{Alerts.getAlertName(alert.alertType)}</td>
                                                <td>{alert.threshold}</td>
                                                <td><button onClick={this.deleteAlert.bind(this, alert._links.self.href)}>Delete</button></td>
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

export default Alerts;
