import React, {Component} from 'react';
import {Switch, BrowserRouter, Route, Redirect} from 'react-router-dom';
import {Container} from 'reactstrap';
import Header from '../../components/Header/';
import Sidebar from '../../components/Sidebar/';
import Breadcrumb from '../../components/Breadcrumb/';
import Aside from '../../components/Aside/';
import Footer from '../../components/Footer/';

import Colors from '../../views/Theme/Colors/';
import Typography from '../../views/Theme/Typography/';

import Charts from '../../views/Charts/';
import Widgets from '../../views/Widgets/';

// Base
import Cards from '../../views/Base/Cards/';
import Forms from '../../views/Base/Forms/';
import Switches from '../../views/Base/Switches/';
import Tables from '../../views/Base/Tables/';
import Tabs from '../../views/Base/Tabs/';
import Breadcrumbs from '../../views/Base/Breadcrumbs/';
import Carousels from '../../views/Base/Carousels/';
import Collapses from '../../views/Base/Collapses/';
import Dropdowns from '../../views/Base/Dropdowns/';
import Jumbotrons from '../../views/Base/Jumbotrons/';
import ListGroups from '../../views/Base/ListGroups/';
import Navbars from '../../views/Base/Navbars/';
import Navs from '../../views/Base/Navs/';
import Paginations from '../../views/Base/Paginations/';
import Popovers from '../../views/Base/Popovers/';
import ProgressBar from '../../views/Base/ProgressBar/';
import Tooltips from '../../views/Base/Tooltips/';

// Buttons
import Buttons from '../../views/Buttons/Buttons/';
import ButtonDropdowns from '../../views/Buttons/ButtonDropdowns/';
import ButtonGroups from '../../views/Buttons/ButtonGroups/';
import SocialButtons from '../../views/Buttons/SocialButtons/';

// Icons
import Flags from '../../views/Icons/Flags/';
import FontAwesome from '../../views/Icons/FontAwesome/';
import SimpleLineIcons from '../../views/Icons/SimpleLineIcons/';

// Notifications
import Badges from '../../views/Notifications/Badges/';
import Modals from '../../views/Notifications/Modals/';

// Dashboard
import Farms from '../../views/Dashboard/Farms/';
import Farm from '../../views/Dashboard/Farms/Farm';
import NewFarm from '../../views/Dashboard/Farms/NewFarm';
import Sensors from '../../views/Dashboard/Sensors/';
import Sensor from '../../views/Dashboard/Sensors/Sensor';
import NewSensor from '../../views/Dashboard/Sensors/NewSensor';
import Alerts from '../../views/Dashboard/Alerts/';
import Alert from '../../views/Dashboard/Alerts/Alert';
import NewAlert from '../../views/Dashboard/Alerts/NewAlert/NewAlert';

// Account Management
import Register from '../../views/Pages/Register/'
import Login from '../../views/Pages/Login/'

class Full extends Component {
    
    componentDidMount() {
        this.checkAuthentication();
    };

    checkAuthentication() {
        let xhr = new XMLHttpRequest();
        xhr.open("GET", 'http://deti-engsoft-07.ua.pt:8080/oauth/check_token?token=' + localStorage.getItem('token'), true);
        xhr.setRequestHeader("Content-type", "application/json");
        xhr.setRequestHeader("Authorization", "Basic c21hcnRmYXJtOnNtYXJ0ZmFybQ==");
        xhr.onload = function () {
            if (this.status < 200 || this.status >= 300) {
                document.location.href = "/#/login";
            }
        };
        xhr.send();
    }

    render() {
        return (
            <div className="app">
                <Header/>
                <div className="app-body">
                    <Sidebar {...this.props}/>
                    <main className="main">
                        <Breadcrumb/>
                        <Container fluid>
                            <Switch>
                                <Route path="/register" name="Register" component={Register}/>
                                <Route path="/login" name="Login" component={Login}/>
                                <Route path="/farms/new" name="New Farm" component={NewFarm}/>
                                <Route exact path="/farms" name="Farms" component={Farms}/>
                                <Route path="/farms/:number" name="Farm" component={Farm}/>
                                <Route path="/sensors/new" name="New Sensor" component={NewSensor}/>
                                <Route exact path="/sensors" name="Sensors" component={Sensors}/>
                                <Route path="/sensors/:number" name="Sensor" component={Sensor}/>
                                <Route path="/alerts/new" name="New Alert" component={NewAlert}/>
                                <Route exact path="/alerts" name="Alerts" component={Alerts}/>
                                <Route path="/alerts/:number" name="Alert" component={Alert}/>
                                <Route path="/theme/colors" name="Colors" component={Colors}/>
                                <Route path="/theme/typography" name="Typography" component={Typography}/>
                                <Route path="/base/cards" name="Cards" component={Cards}/>
                                <Route path="/base/forms" name="Forms" component={Forms}/>
                                <Route path="/base/switches" name="Switches" component={Switches}/>
                                <Route path="/base/tables" name="Tables" component={Tables}/>
                                <Route path="/base/tabs" name="Tabs" component={Tabs}/>
                                <Route path="/base/breadcrumbs" name="Breadcrumbs" component={Breadcrumbs}/>
                                <Route path="/base/carousels" name="Carousels" component={Carousels}/>
                                <Route path="/base/collapses" name="Collapses" component={Collapses}/>
                                <Route path="/base/dropdowns" name="Dropdowns" component={Dropdowns}/>
                                <Route path="/base/jumbotrons" name="Jumbotrons" component={Jumbotrons}/>
                                <Route path="/base/list-groups" name="ListGroups" component={ListGroups}/>
                                <Route path="/base/navbars" name="Navbars" component={Navbars}/>
                                <Route path="/base/navs" name="Navs" component={Navs}/>
                                <Route path="/base/paginations" name="Paginations" component={Paginations}/>
                                <Route path="/base/popovers" name="Popovers" component={Popovers}/>
                                <Route path="/base/progress-bar" name="Progress Bar" component={ProgressBar}/>
                                <Route path="/base/tooltips" name="Tooltips" component={Tooltips}/>
                                <Route path="/buttons/buttons" name="Buttons" component={Buttons}/>
                                <Route path="/buttons/button-dropdowns" name="ButtonDropdowns"
                                       component={ButtonDropdowns}/>
                                <Route path="/buttons/button-groups" name="ButtonGroups" component={ButtonGroups}/>
                                <Route path="/buttons/social-buttons" name="Social Buttons" component={SocialButtons}/>
                                <Route path="/icons/flags" name="Flags" component={Flags}/>
                                <Route path="/icons/font-awesome" name="Font Awesome" component={FontAwesome}/>
                                <Route path="/icons/simple-line-icons" name="Simple Line Icons"
                                       component={SimpleLineIcons}/>
                                <Route path="/notifications/badges" name="Badges" component={Badges}/>
                                <Route path="/notifications/modals" name="Modals" component={Modals}/>
                                <Route path="/widgets" name="Widgets" component={Widgets}/>
                                <Route path="/charts" name="Charts" component={Charts}/>
                            </Switch>
                        </Container>
                    </main>
                    <Aside/>
                </div>
                <Footer/>
            </div>
        );
    }
}

export default Full;
