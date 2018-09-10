import React, {Component} from 'react';

import {
    Row,
    Col,
    Card,
    CardBody,
    CardTitle,
    Table
} from 'reactstrap';

class Farms extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            farms : [],
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

    loadFarms() {
        fetch('http://deti-engsoft-07.ua.pt:8000/farms?access_token=' + localStorage.getItem('token')).then(results => {
            return results.json();
        }).then(values => {
            this.setState({farms: values['_embedded']['farms']});
        });
    }

    deleteFarm(farmId){
        let xhr = new XMLHttpRequest();
        xhr.open("DELETE", 'http://deti-engsoft-07.ua.pt:8000/farms/' + farmId + '?access_token=' + localStorage.getItem('token'), true);
        xhr.setRequestHeader("Content-type", "application/json");
        xhr.setRequestHeader("Authorization", "Bearer " + localStorage.getItem('token'));
        xhr.onload = function () {
            if(this.status > 200 && this.status < 300){
                location.reload();
            }
        };
        xhr.send();
    }

    static getId(farm) {
        let farmLink = farm.split('/');
        return farmLink[farmLink.length - 1];
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
                                        <CardTitle className="mb-0">Farms</CardTitle>
                                        <div className="small text-muted">March 2018</div>
                                    </Col>
                                </Row>
                                <br/>
                                <Table bordered>
                                    <thead>
                                    <tr>
                                        <th>Name</th>
                                        <th>Address</th>
                                        <th>Area</th>
                                        <th>Longitude</th>
                                        <th>Latitude</th>
                                        <th>Delete</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    {
                                        this.state.farms.map((object, key) =>
                                            <tr key={key}>
                                                <th scope="row"><a href={"/#/farms/" + Farms.getId(object._links.self.href)}>
                                                    {object.name}</a></th>
                                                <td>{object.address}</td>
                                                <td>{object.area}</td>
                                                <td>{object.longitude}</td>
                                                <td>{object.latitude}</td>
                                                <td><button onClick={this.deleteFarm.bind(this, object._links.self.href.split('farms/')[1])}>Delete</button></td>
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

export default Farms;
