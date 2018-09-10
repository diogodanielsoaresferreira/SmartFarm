import React, {Component} from 'react';
import Highcharts from 'highcharts/highstock';
import SockJS from 'sockjs-client';
import Stomp from '@stomp/stompjs';
import GoogleMapReact from 'google-map-react';
import marker from '../../../../../public/img/marker.png'

import {
    Row,
    Col,
    Card,
    CardBody,
    CardTitle,
    Button,
    Form,
    FormGroup,
    Label,
    Input,
} from 'reactstrap';

const Marker = ({  img_src }) => <div><img src={img_src} style={{transform: "translate(-50%, -50%)"}}/></div>;

class Sensor extends React.Component {
    constructor(props) {
        super(props);

        this.handleChange = this.handleChange.bind(this);

        this.state = {
            sensorData : [],
            valueAct: 0,
            sensorAverage: 0,
            zoom: 12
        };

        this.sensorId = this.props.match.params.number;

        Highcharts.setOptions({
            global: {
                useUTC: false
            }
        });
        this.sendValueActuator = this.sendValueActuator.bind(this);
    }

    componentDidMount() {
        this.checkAuthentication();
        this.loadSensorData();
        this.loadSensorHistoric();
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

    loadSensorData() {
        fetch('http://deti-engsoft-07.ua.pt:8000/sensors/' + this.sensorId + '?access_token=' + localStorage.getItem('token')).then(results => {
            return results.json();
        }).then(values => {
            this.setState({
                name: values.name,
                type: values.type,
                model: values.model,
                actuator: values.actuator,
                status: values.status,
                farm: values.farm,
                created: (new Date(values.dateCreated)).toISOString().replace('-', '/').split('T')[0].replace('-', '/'),
                lat: values.latitude,
                lng: values.longitude,
            })

            this.loadFarmSensor(values.farm);
        });
    }

    loadFarmSensor(valuesFarmId) {
        fetch('http://deti-engsoft-07.ua.pt:8000/farms/' + valuesFarmId + '?access_token=' + localStorage.getItem('token')).then(results => {
            return results.json();
        }).then(values => {
            this.setState({
                farmName: values.name
            })
        });
    }

    handleChange(event) {
        this.setState({[event.target.name]: event.target.value});
    }

    sendValueActuator(event){
        let name = '';

        event.preventDefault();

        let xhr = new XMLHttpRequest();
        xhr.open("POST", 'http://deti-engsoft-07.ua.pt:8000/actuate/' + this.sensorId + '?access_token=' + localStorage.getItem('token'), true);
        xhr.setRequestHeader("Content-type", "application/json");
        xhr.onload = function () {
            if(this.status >= 200 && this.status < 300){
                console.log("Value sent!");
            }else{
                console.log("Something went wrong!");
            }
        };
        
        xhr.send(
            JSON.stringify({
                value: this.state.valueAct
            })
        );
    }

    createCharts() {
        this.chart = Highcharts.stockChart('container', {
            /*rangeSelector: {
                buttons: [{
                    count: 1,
                    type: 'minute',
                    text: '1M'
                }, {
                    count: 5,
                    type: 'minute',
                    text: '5M'
                }, {
                    type: 'all',
                    text: 'All'
                }],
                inputEnabled: false,
                selected: 2
            },*/

            title: {
                text: 'Sensor values'
            },
            yAxis: {
                plotLines: [{
                    color: 'green',
                    value: this.state.sensorAverage,
                    dashStyle: 'shortdash',
                    width: '2',
                    label: {
                        text: 'Average = ' + this.state.sensorAverage.toFixed(2)
                    }
                }]
            },
            exporting: {
                enabled: false
            },
            series: [{
                name: 'Value',
                data: this.state.sensorData
            }]
        });
    }

    loadSensorHistoric() {
        fetch('http://deti-engsoft-07.ua.pt:8000/persistence/historic/' + this.sensorId
        ).then(results => {
            return results.json();
        }).then(values => {
            for (let v in values) {
                // add new sensor value and timestamp
                this.state.sensorData.push([
                    (new Date(values[v].timestamp)).getTime(),
                    parseFloat(values[v].value)
                ]);    
            }

            // add default value so that the chart can be built
            if (values.length == 0)
                this.state.sensorData.push([
                    (new Date()).getTime(),
                    0.0
                ]);
            
            // load sensor average value
            this.loadSensorAverage();
        });
    }

    loadSensorAverage() {
        fetch('http://deti-engsoft-07.ua.pt:8000/persistence/averages/' + this.sensorId
        ).then(results => {
            return results.json();
        }).then(values => {
            if (values.length)
                this.setState({
                    sensorAverage: parseFloat(values[values.length - 1].value)
                });


            // create charts and connect to socket
            this.createCharts();
            this.socketConnect();
        });
    }

    socketConnect() {
        // connect to smartfarm websocket
        let socket = new SockJS('http://deti-engsoft-07.ua.pt:8000/smartfarm?access_token=' + localStorage.getItem('token'));
        let ws = Stomp.over(socket);

        // disable debug logs
        ws.debug = null;

        let self = this;
        ws.connect({}, function () {
            ws.subscribe("/topic/sensors", function (sensor) {
                sensor = JSON.parse(sensor.body);

                if (sensor.id === self.sensorId && Number(parseFloat(sensor.value)) === parseFloat(sensor.value)) {
                    // add new sensor value and timestamp
                    self.chart.series[0].addPoint(
                        [
                            (new Date(sensor.timestamp)).getTime(),
                            parseFloat(sensor.value)
                        ],
                        true,
                        false
                    );
                }
            });
        }, function (error) {
            console.log(error);
        });
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
                                        <div className="small text-muted">March 2018</div>
                                    </Col>
                                </Row>
                                <br/>
                                <div id="container"></div>
                                <br/>
                                <p><b>Associated Farm</b>: {this.state.farmName}</p>
                                <p><b>Type</b>: {this.state.type}</p>
                                <p><b>Model</b>: {this.state.model}</p>
                                <p><b>Actuator</b>: {String(this.state.actuator)}</p>
                                <Form onSubmit={this.sendValueActuator}>
                                    <FormGroup>
                                        <Label for="exampleSelect">Send value to actuator</Label>
                                        <Input type="number" name="valueAct" min="0" value={this.state.valueAct} onChange={this.handleChange}/>
                                    </FormGroup>
                                    <Button>Send value</Button>
                                </Form>
                                <br/>
                                <p><b>Status</b>: {this.state.status ? this.state.status : '--'}</p>
                                <p><b>Creation date</b>: {this.state.created}</p>
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
                            </CardBody>
                        </Card>
                    </Col>
                </Row>
            </div>
        )
    }
}

Sensor.defaultProps = {
    center: { lat: 40.7446790, lng: -73.9485420 },
    zoom: 11
};


export default Sensor;
