import logo from './logo.svg';
import './App.css';
import App from './App';
import ReactDOM from 'react-dom/client';


//import {Link} from "react-router-dom"; 

import React, { useState, useEffect } from 'react';


class Survey extends React.Component {
  constructor(props) {
    super(props);
    this.state = { questions: [] ,answers: [], textValue:"",showComponent:false };
  }
  componentDidMount() { 
    fetch('http://localhost:8080/get-questions')
      .then((response) => response.json())
      .then((data) => {
         this.setState({ questions: data })
      })
      .catch((err) => {
         console.log(err.message);
      });
    
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

  updateInputValue = (evt) => {
    this.setState({
      textValue: evt.target.value
    });
  };

  setAnswer = (evt) => {
    let ss= evt.target.value
    let qq= ss.substring(ss.indexOf('#')+1)
    let ans= ss.substring(0,ss.indexOf('#'))
    let flag= false
    this.state.answers.forEach(aa=>{
      if(aa.question == qq){
        flag = true
        aa.answer = ans
      }
    })
    if(!flag)
      this.state.answers.push({question:qq,answer:ans})
    this.setState({ answers: this.state.answers });
    console.log(this.state.answers);
  };

  handleClick = (evt) => {
    console.log('*** handleClick ***')
    let obj ={}
    this.state.answers.forEach(aa=>{
      obj[aa.question] = aa.answer
    })
    console.log(obj)

    fetch('http://localhost:8080/submit-questions', {
         method: 'POST',
         body: JSON.stringify(obj),
         headers: {
            'Content-type': 'application/json',
         },
      })
         //.then((res) => res.json())
         .then((post) => {
            console.log('sent...')
            alert('Survey submited..')
            const root = ReactDOM.createRoot(document.getElementById('root'));
            root.render(
              <App />
            );

         })
         .catch((err) => {
            console.log(err.message);
         });

  };

  render() {
    return (
      <div>
        <h1>Questions : </h1>
        {this.state.questions.map((question,i) => <div onChange={this.setAnswer.bind(this)}>
          {question.name} 
          <input type="radio" value={"Yes#"+question.name} name={i}/> Yes
          <input type="radio" value={"No#"+question.name} name={i}/> No
          </div> 
          )}
        <div><a href="#" onClick={this.onLinkClick}>Add Question : </a><input type="text" value ={this.state.textValue} name ="question" onChange={this.updateInputValue}/></div>
        <div style={{ whiteSpace: "pre" }}> </div>
        <div><button onClick={this.handleClick}>Submit Survey</button></div>
        <div style={{ whiteSpace: "pre" }}> </div>
        <div><a href="http://localhost:3000/">Home page</a></div>
      </div>
    );
  }
}

export default Survey;
