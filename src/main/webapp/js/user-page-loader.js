/*
 * Copyright 2019 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// Get ?user=XYZ parameter value
const urlParams = new URLSearchParams(window.location.search);
const parameterUsername = urlParams.get('user');

// URL must include ?user=XYZ parameter. If not, redirect to homepage.
if (!parameterUsername) {
  window.location.replace('/');
}

/** Sets the page title based on the URL parameter username. */
function setPageTitle() {
  document.getElementById('page-title').innerText = parameterUsername;
  document.title = parameterUsername + ' - User Page';
}

/**
 * Shows the message form if the user is logged in and allows user to specify whom to send a message to.
 */
function showMessageFormIfLoggedIn() {
  fetch('/login-status')
    .then((response) => {
      return response.json();
    })
    .then((loginStatus) => {
      if (loginStatus.isLoggedIn) {
        const messageForm = document.getElementById('message-form');
        messageForm.action = '/messages?recipient=' + parameterUsername;
        messageForm.classList.remove('hidden');
      }
    });
}

/** Fetches messages and add them to the page. */
function fetchMessages() {
  const url = '/messages?user=' + parameterUsername;
  fetch(url)
    .then((response) => {
      return response.json();
    })
    .then((messages) => {
      const messagesContainer = document.getElementById('message-container');
      if (messages.length == 0) {
        messagesContainer.innerHTML = '<p>There are no messages between you and this user yet. Say hi!</p>';
      } else {
        messagesContainer.innerHTML = '';
      }
      messages.forEach((message) => {
        const messageDiv = buildMessageDiv(message);
        messagesContainer.appendChild(messageDiv);
      });
    });
}

/**
 * Builds an element that displays the message.
 * @param {Message} message
 * @return {Element}
 */
function buildMessageDiv(message) {
  const headerDiv = document.createElement('div');
  headerDiv.classList.add('message-header');
  headerDiv.appendChild(document.createTextNode(
    message.user + ' - ' + new Date(message.timestamp)));

  const bodyDiv = document.createElement('div');
  bodyDiv.classList.add('message-body');
  bodyDiv.innerHTML = message.text;

  const messageDiv = document.createElement('div');
  messageDiv.classList.add('message-div');
  messageDiv.appendChild(headerDiv);
  messageDiv.appendChild(bodyDiv);

  return messageDiv;
}

/** Fetches data and populates the UI of the page. */
function buildUI() {
  setPageTitle();
  showMessageFormIfLoggedIn();
  fetchMessages();
}

var onSubmit = function(data) {
    var XHR = new XMLHttpRequest();
    var FD  = new FormData();
    var url = "/api/user-form-data";
  
   
  
    // Define what happens on successful data submission
    XHR.addEventListener('load', function(event) {
      alert('Yeah! Data sent and response loaded.');
    });
  
    // Define what happens in case of error
    XHR.addEventListener('error', function(event) {
      alert('Oops! Something went wrong.');
    });
  
    // Set up our request
    XHR.open('POST', url);
  
    // Send our FormData object; HTTP headers are set automatically
    XHR.send(FD);
  }


var handleSuccessfulResponse = function(response) {
  console.log('Response was successful');
  // ... do stuff with valid response ...
};

var handleErrorResponse = function(response) {
  console.log('Response was unsuccessful'); 
  console.log('Status code: ' + response.status);
  console.log('Status message: ' + response.statusText);
  throw response;
  // ... do stuff with error response ...
};

// A version of "fetch" that returns a Promise that splits
// responses so that "then" only receives successful responses
// and "catch" only receives unsuccessful responses.

var fetchWithErrorHandling = function(url) {
 return fetch(url).then(function(response) {
   if (!response.ok) {
     console.log('Response is not OK');
     throw response;
   }
   console.log('Response is OK');
   return response;
 });
};
 
// // ...
// var eventualResponse = fetchWithErrorHandling(someUrl);
// eventualResponse.then(handleSuccessfulResponse);
// eventualResponse.catch(handleErrorResponse);
// // ...   