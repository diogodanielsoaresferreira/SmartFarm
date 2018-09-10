import React, {Component} from 'react';
import GoogleMapReact from 'google-map-react';
import marker from '../../../../../public/img/marker.png'

import {
    Row,
    Col,
    Card,
    CardBody,
    CardTitle,
    Table
} from 'reactstrap';

const Marker = ({  img_src }) => <div><img src={img_src} style={{transform: "translate(-50%, -50%)"}}/></div>;

class Farm extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            zoom: 12,
            sensors: []
        }

        this.farmId = this.props.match.params.number;
    }

    componentDidMount() {
        this.checkAuthentication();
        this.loadFarmData();
        this.loadFarmSensors();
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

    loadFarmData() {
        fetch('http://deti-engsoft-07.ua.pt:8000/farms/' + this.farmId + '?access_token=' + localStorage.getItem('token')).then(results => {
            return results.json();
        }).then(values => {
            this.setState({
                name: values.name,
                address : values.address,
                area: values.area,
                created: (new Date(values.dateCreated)).toISOString().replace('-', '/').split('T')[0].replace('-', '/'),
                lat: values.latitude,
                lng: values.longitude,
            })
        });
    }

    loadFarmSensors() {
        fetch('http://deti-engsoft-07.ua.pt:8000/sensors?access_token=' + localStorage.getItem('token')).then(results => {
            return results.json();
        }).then(values => {
            let self = this;
            
            let listSensors = values['_embedded']['sensors'];
            let farmSensors = listSensors.filter(function(sensor) {
                return sensor.farm == self.farmId;
            });
            
            this.setState({sensors: farmSensors});
        });
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
                                        <CardTitle className="mb-0">{this.state.name}</CardTitle>
                                        <div className="small text-muted">Farm ID: {this.farmId}</div>
                                    </Col>
                                </Row>
                                <br/>
                                <p><b>Area</b>: {this.state.area} m²</p>
                                <p><b>Creation date</b>: {this.state.created}</p>
                                <p><b>Address</b>: {this.state.address}</p>
                                <p><b>Coordinates</b>:</p>
                                <div style={{height: '40vh', width: '40%', float: 'none', margin: '0 auto'}}>
                                    <GoogleMapReact
                                        bootstrapURLKeys={{key: 'AIzaSyByH0c5bxYDZ48BLQ401BBsm4DppG6QNkQ'}}
                                        center={[this.state.lat, this.state.lng]}
                                        defaultZoom={this.state.zoom}>
                                        <Marker
                                            lat={this.state.lat}
                                            lng={this.state.lng}
                                            img_src={marker}/>
                                    </GoogleMapReact>
                                </div>
                                <br/>
                                <p><b>Farm sensors</b>:</p>
                                <Table bordered>
                                    <thead>
                                    <tr>
                                        <th>Device ID</th>
                                        <th>Name</th>
                                        <th>Type</th>
                                        <th>Model</th>
                                        <th>Actuator</th>
                                        <th>Active</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    {
                                        this.state.sensors.map((object, key) =>
                                            <tr key={key}>
                                                <th scope="row"><a href={"/#/sensors/" + Farm.getId(object._links.self.href)}>
                                                    {Farm.getId(object._links.self.href)}</a></th>
                                                <td>{object.name}</td>
                                                <td>{object.type}</td>
                                                <td>{object.model}</td>
                                                <td>{String(object.actuator)}</td>
                                                <td>{String(object.active)}</td>
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

export default Farm;
