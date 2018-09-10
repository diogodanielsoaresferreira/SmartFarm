import React, {Component} from 'react';
import {Container, Row, Col, CardGroup, Card, CardBody, Button, Input, InputGroup, InputGroupAddon, InputGroupText} from 'reactstrap';


class Login extends Component {

	constructor(props) {
		super(props);

		this.handleChange = this.handleChange.bind(this);

		this.state = {
			username: '',
			password: '',
		};

		this.login = this.login.bind(this);
	}

	componentDidMount() {
        this.checkAuthentication();
    };

    checkAuthentication() {
        let xhr = new XMLHttpRequest();
        xhr.open("GET", 'http://deti-engsoft-07.ua.pt:8000/oauth/check_token?token=' + localStorage.getItem('token'), true);
        xhr.setRequestHeader("Content-type", "application/json");
        xhr.setRequestHeader("Authorization", "Basic c21hcnRmYXJtOnNtYXJ0ZmFybQ==");
        xhr.onload = function () {
            if (this.status >= 200 && this.status < 300) {
                document.location.href = "/#/";
            }
        };
        xhr.send();
    }

	handleChange(event) {
        this.setState({[event.target.name]: event.target.value});
    }

	login(event) {
		event.preventDefault();

		let name = this.state.username;

        let xhr = new XMLHttpRequest();

        xhr.open("POST", "http://deti-engsoft-07.ua.pt:8000/oauth/token?grant_type=password&username=" + this.state.username + "&password=" + this.state.password, true);
        xhr.setRequestHeader("Content-Type", "application/json");
        xhr.setRequestHeader("Authorization", "Basic c21hcnRmYXJtOnNtYXJ0ZmFybQ==");
        xhr.onload = function () {
        	if (this.status >= 200 && this.status < 300) {
                var jsonResponse = JSON.parse(xhr.responseText);
                var accessToken = jsonResponse.access_token;
                localStorage.setItem('token', accessToken);
                localStorage.setItem('user', name);
                document.location.href = "/#/";
            }
            else {
                alert ("Login was unsuccessful, please check your username and password");
                document.location.reload();
            }
        };
        xhr.send();
    
    }

	render() {
		return (
			<div className="app flex-row align-items-center">
				<Container>
					<Row className="justify-content-center">
						<Col md="8">
							<CardGroup>
								<Card className="p-4">
									<CardBody>
										<h1>Login</h1>
										<p className="text-muted">Sign In to your <b>Smartfarm</b> account</p>
										<InputGroup className="mb-3">
											<InputGroupAddon addonType="prepend">
												<InputGroupText>
													<i className="icon-user"></i>
												</InputGroupText>
											</InputGroupAddon>
											<Input type="text" name ="username" placeholder="Email" onChange={this.handleChange}/>
										</InputGroup>
										<InputGroup className="mb-4">
											<InputGroupAddon addonType="prepend">
												<InputGroupText>
													<i className="icon-lock"></i>
												</InputGroupText>
											</InputGroupAddon>
											<Input type="password" name="password" placeholder="Password" onChange={this.handleChange}/>
										</InputGroup>
										<Row>
											<Col xs="6">
												<Button onClick={this.login} color="primary" className="px-4">Login</Button>
											</Col>
										</Row>
									</CardBody>
								</Card>
								<Card className="text-white bg-primary py-5 d-md-down-none" style={{ width: 44 + '%' }}>
									<CardBody className="text-center">
										<div>
											<h2>Sign up</h2>
											<p>Create now your account and remotely manage all your tillable areas.</p>
											<Button href="/#/register" color="primary" className="mt-3" active>Register Now!</Button>
										</div>
									</CardBody>
								</Card>
							</CardGroup>
						</Col>
					</Row>
				</Container>
			</div>
		);
	}
}

export default Login;
