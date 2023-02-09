import logo from './logo.svg';
import './App.css';



//import {Link} from "react-router-dom"; 

import React, { useState, useEffect } from 'react';


class App extends React.Component {
  constructor(props) {
    super(props);
  }
  
  render() {
    return (
      <div>
        <div><a href="survey">Take Survey</a></div>
        <div><a href="report">Show Report</a></div>
      </div>
    );
  }
}

export default App;
