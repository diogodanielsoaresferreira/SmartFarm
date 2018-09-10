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


class NewFarm extends React.Component {
    constructor(props) {
        super(props);

        this.handleChange = this.handleChange.bind(this);
        this.addNewFarm = this.addNewFarm.bind(this);
        this.setLocation = this.setLocation.bind(this);
        this.toggle = this.toggle.bind(this);

        this.state = {
            name: '',
            address: '',
            area: 0,
            marker : { lat: 40.630571, lng: -8.655915, img: marker },
            tooltipOpen: false
        };
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

    handleChange(event) {
        this.setState({[event.target.name]: event.target.value});
    }

    toggle() {
        this.setState({tooltipOpen: !this.state.tooltipOpen});
    }

    addNewFarm(e) {
        e.preventDefault();

        let xhr = new XMLHttpRequest();
        xhr.open("POST", 'http://deti-engsoft-07.ua.pt:8000/farms?access_token=' + localStorage.getItem('token'), true); // + localStorage.getItem('token'), true);
        xhr.setRequestHeader("Content-type", "application/json");
        xhr.onload = function () {
            if(this.status > 200 && this.status < 300)
                document.location.href = "/#/farms";
        };
        xhr.send(
            JSON.stringify({
                name: this.state.name,
                address: this.state.address,
                area: this.state.area,
                longitude: this.state.marker.lng,
                latitude: this.state.marker.lat
            })
        )
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
                        <Form onSubmit={this.addNewFarm}>
                            <FormGroup>
                                <Label for="exampleSelect">Name</Label>
                                <Input type="text" name="name" onChange={this.handleChange}/>
                            </FormGroup>
                            <FormGroup>
                                <Label for="exampleNumber">Address</Label>
                                <Input type="text" name="address" onChange={this.handleChange}/>
                            </FormGroup>
                            <FormGroup>
                                <Label for="exampleNumber">Area (m²)</Label>
                                <Input type="number" name="area" onChange={this.handleChange}/>
                            </FormGroup>
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
                            <Button>Add new farm</Button>
                        </Form>
                    </CardBody>
                    </Card>
                </Col>
                </Row>
            </div>
        )
    }
}

NewFarm.defaultProps = {
    center: { lat: 40.630571, lng: -8.655915 },
    zoom: 12
};

export default NewFarm;
