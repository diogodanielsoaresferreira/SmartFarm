import React, {Component} from 'react';
import GoogleMapReact from 'google-map-react';
import marker from '../../../../../public/img/marker.png'

import {
    Row,
    Col,
    Card,
    CardBody,
    Button,
    Label,
    Input,
    Form,
    FormGroup,
    Tooltip
} from 'reactstrap';

const Marker = ({  img_src }) => <div><img src={img_src} style={{transform: "translate(-50%, -50%)"}}/></div>;

class NewSensor extends React.Component {
    constructor(props) {
        super(props);

        this.handleChange = this.handleChange.bind(this);
        this.handleSelectChange = this.handleSelectChange.bind(this);
        this.handleCheckbox = this.handleCheckbox.bind(this);
        this.addNewSensor = this.addNewSensor.bind(this);
        this.setLocation = this.setLocation.bind(this);
        this.toggle = this.toggle.bind(this);

        this.state = {
            name: '',
            type: 'Temperature',
            model: '',
            actuator: false,
            marker: { lat: 40.630571, lng: -8.655915, img: marker },
            farms: [],
            farmId: '',
            number: 1,
            tooltipOpen: false
        };
    }

    componentDidMount() {
        this.checkAuthentication();
        this.loadFarms();
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

    handleSelectChange(event) {
        let farm = this.state.farms.filter(function(farm) {
            return farm.name === event.target.value;
        })[0];
        let farmLink = farm._links.self.href.split('/');

        this.setState({[event.target.name]: farmLink[farmLink.length - 1]});
    }

    handleCheckbox(event) {
        this.setState({[event.target.name]: event.target.checked});
    }

    toggle() {
        this.setState({tooltipOpen: !this.state.tooltipOpen});
    }

    loadFarms() {
        fetch('http://deti-engsoft-07.ua.pt:8080/farms?access_token=' + localStorage.getItem('token')).then(results => {
            return results.json();
        }).then(values => {
            this.setState({farms: values['_embedded']['farms']});

            // update state with first deviceId
            let farmLink = this.state.farms[0]._links.self.href.split('/');
            this.setState({farmId: farmLink[farmLink.length - 1]});
        })
    }

    addNewSensor(e) {
        let name = '';

        e.preventDefault();

        for (var i = 0; i < this.state.number; i++) {
            let xhr = new XMLHttpRequest();
            xhr.open("POST", 'http://deti-engsoft-07.ua.pt:8000/sensors?access_token=' + localStorage.getItem('token'), true);
            xhr.setRequestHeader("Content-type", "application/json");
            xhr.onload = function () {
                if(this.status > 200 && this.status < 300){
                    document.location.href = "/#/sensors";
                }
            };

            name = this.state.number === 1 ? this.state.name : 
                                this.state.name + ' (' + (i + 1) + ')';

            xhr.send(
                JSON.stringify({
                    name: name,
                    model: this.state.model,
                    type: this.state.type,
                    actuator: this.state.actuator,
                    longitude: this.state.marker.lng,
                    latitude: this.state.marker.lat,
                    farm: this.state.farmId
                })
            );
        }
    }
        
    setLocation(e) {
        this.setState({marker: {lat: e.lat, lng: e.lng, img: this.state.marker.img}});
    }

    render() {
        return (
            <div className="animated fadeIn">
                <Row>
                <Col>
                    <Card>
                    <CardBody>
                        <Form onSubmit={this.addNewSensor}>
                            <FormGroup>
                                <Label for="exampleSelect">Associated Farm</Label>
                                <Input type="select" name="farmId" onChange={this.handleSelectChange}>
                                    { this.state.farms.map((farm, key) =>
                                            <option key={key}>{farm.name}</option>
                                        )
                                    }
                                </Input>
                            </FormGroup>
                            <FormGroup>
                                <Label for="exampleSelect">Name</Label>
                                <Input type="text" name="name" onChange={this.handleChange}/>
                            </FormGroup>
                            <FormGroup>
                                <Label for="exampleSelect">Type</Label>
                                <Input type="select" name="type" onChange={this.handleChange}>
                                    <option>Temperature</option>
                                    <option>Humidity</option>
                                    <option>UV Radiation</option>
                                </Input>
                            </FormGroup>
                            <FormGroup>
                                <Label for="exampleSelect">Model</Label>
                                <Input type="text" name="model" onChange={this.handleChange}/>
                            </FormGroup>
                            <FormGroup>
                                <Label for="exampleSelect">Number of sensors</Label>
                                <Input type="number" name="number" min="1" value={this.state.number} onChange={this.handleChange}/>
                            </FormGroup>
                            <FormGroup check>
                                <Label check>
                                    <Input type="checkbox" name="actuator" onClick={this.handleCheckbox} />{' '}
                                    Actuator
                                </Label>
                            </FormGroup>
                            <br/>
                            <FormGroup>
                                <Label id="coordinates" for="exampleNumber">Coordinates (?)</Label>
                                <Tooltip placement="right" isOpen={this.state.tooltipOpen} target="coordinates" toggle={this.toggle}>
                                    Place a marker on the map to register the coordinates
                                </Tooltip>
                                <div style={{ height: '60vh', width: '60%', float: 'none', margin: '0 auto'}}>
                                    <GoogleMapReact
                                        bootstrapURLKeys={{key: 'AIzaSyByH0c5bxYDZ48BLQ401BBsm4DppG6QNkQ'}}
                                        defaultCenter={this.props.center}
                                        defaultZoom={this.props.zoom}
                                        onClick={this.setLocation}>
                                            <Marker
                                                lat={this.state.marker.lat}
                                                lng={this.state.marker.lng}
                                                img_src={this.state.marker.img}/>
                                    </GoogleMapReact>
                                </div>
                            </FormGroup>
                            <br/>
                            <Button>Add new sensor</Button>
                        </Form>
                    </CardBody>
                    </Card>
                </Col>
                </Row>
            </div>
        )
    }
}

NewSensor.defaultProps = {
    center: { lat: 40.630571, lng: -8.655915 },
    zoom: 12
};

export default NewSensor;
