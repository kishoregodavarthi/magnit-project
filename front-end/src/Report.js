
import React, { useState, useEffect } from 'react';


class Report extends React.Component {
  constructor(props) {
    super(props);
    this.state = { questions: [] ,answers: [], textValue:"" };
  }
  onLinkClick = (e) => {
    if(!this.state.textValue){
      return alert('Enter some question!')
    }
    this.state.questions.push({'name':this.state.textValue})
    this.setState({
      textValue: ''
    });
    this.setState({ questions: this.state.questions });
  };

  render() {
    return (
      <div>
        <div><a href="http://localhost:8080/get-report?reportType=txt">Report in txt format</a></div>
        <div><a href="http://localhost:8080/get-report?reportType=xlsx">Report in excel format</a></div>
        <div style={{ whiteSpace: "pre" }}> </div>
        <div><a href="http://localhost:3000/">Home page</a></div>
      </div>
    );
  }
}

export default Report;
