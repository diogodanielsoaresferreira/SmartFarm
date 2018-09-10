import React, {Component} from 'react';
import {
  Badge,
  DropdownItem,
  DropdownMenu,
  DropdownToggle,
  Dropdown
} from 'reactstrap';

class HeaderDropdown extends Component {

  constructor(props) {
    super(props);

    this.toggle = this.toggle.bind(this);

    this.logout = this.logout.bind(this);

    this.state = {
      dropdownOpen: false
    };
  }

  logout(e) {
    e.preventDefault();

    localStorage.setItem('token', '');
    document.location.href = "/#/login";
  }

  toggle() {
    this.setState({
      dropdownOpen: !this.state.dropdownOpen
    });
  }

  dropAccnt() {
    return (
      <Dropdown nav isOpen={this.state.dropdownOpen} toggle={this.toggle}>
        <DropdownToggle nav>
          <img src={'img/avatars/logged-out.png'} className="img-avatar" alt="admin@bootstrapmaster.com"/>
        </DropdownToggle>
        <DropdownMenu right>
          <DropdownItem header tag="div" className="text-center"><strong>Account</strong></DropdownItem>
          {/*<DropdownItem><i className="fa fa-bell-o"></i> Updates<Badge color="info">42</Badge></DropdownItem>
          <DropdownItem><i className="fa fa-envelope-o"></i> Messages<Badge color="success">42</Badge></DropdownItem>*/}
          <DropdownItem onClick={this.logout}><i className="fa fa-lock"></i>Logout</DropdownItem>
        </DropdownMenu>
      </Dropdown>
    );
  }

  render() {
    const {...attributes} = this.props;
    return (
      this.dropAccnt()
    );
  }
}

export default HeaderDropdown;
