import React, {Component} from 'react';
import {Container, Row, Col, Card, CardBody, CardFooter, Button, Input, InputGroup, InputGroupAddon, InputGroupText} from 'reactstrap';


class Register extends Component {
    
    constructor(props) {
        super(props);

        this.handleChange = this.handleChange.bind(this);

        this.state = {
            username: '',
            password: '',
            match: false,
        };

        this.register = this.register.bind(this);
    }

    handleChange(event) {
        this.setState({[event.target.name]: event.target.value});
    }

    register(event) {
		event.preventDefault();

		var tempDate = new Date();
		var date = tempDate.getFullYear() + '-' + (tempDate.getMonth()+1) + '-' + tempDate.getDate();

        let xhr = new XMLHttpRequest();

        xhr.open("POST", "http://deti-engsoft-07.ua.pt:8000/users", true);
        xhr.setRequestHeader("Content-type", "application/json");
        xhr.onload = function () {
        	if (this.status >= 200 && this.status < 300) {
                document.location.href = "/#/login";
            }
            else {
                alert ("Register was unsuccessful, please check your username and password");
                document.location.reload();
            }
        };
        xhr.send(
            JSON.stringify({
            	email : this.state.username,
                password: this.state.password,
                isActive: "false",
                dateJoined: date,
                lastLogin: date
            })
        )
    
    }

    render() {
        return (
          <div className="app flex-row align-items-center">
            <Container>
              <Row className="justify-content-center">
                <Col md="6">
                  <Card className="mx-4">
                    <CardBody className="p-4">
                      <h1>Register</h1>
                      <p className="text-muted">Create your <b>Smartfarm</b> account</p>
                      <InputGroup className="mb-3">
                        <InputGroupAddon addonType="prepend">
                          <InputGroupText>@</InputGroupText>
                        </InputGroupAddon>
                        <Input name="username" type="text" placeholder="Email" onChange={this.handleChange}/>
                      </InputGroup>
                      <InputGroup className="mb-3">
                        <InputGroupAddon addonType="prepend">
                          <InputGroupText>
                            <i className="icon-lock"></i>
                          </InputGroupText>
                        </InputGroupAddon>
                        <Input name="password" type="password" placeholder="Password" onChange={this.handleChange}/>
                      </InputGroup>
                      <Button color="success" onClick={this.register} block>Create Account</Button>
                    </CardBody>
                    <CardFooter className="p-4">
                      <Row>
                        <Col xs="12" sm="6">
                          <Button className="btn-facebook" block><span>facebook</span></Button>
                        </Col>
                        <Col xs="12" sm="6">
                          <Button className="btn-twitter" block><span>twitter</span></Button>
                        </Col>
                      </Row>
                    </CardFooter>
                  </Card>
                </Col>
              </Row>
            </Container>
          </div>
        );
  }
}

export default Register;
