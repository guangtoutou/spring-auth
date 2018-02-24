import React, { Component } from 'react';
import axios from 'axios';

import logo from './logo.svg';

import LoginForm from './components/LoginForm'

class App extends Component {
  constructor(props){
    super(props);
  }
  
  render() {
    return (

        <LoginForm/>

    );
  }
}

export default App;
