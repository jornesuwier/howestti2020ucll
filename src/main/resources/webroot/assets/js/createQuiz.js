"use strict";
/* global EventBus, document, console, messagelist */
document.addEventListener("DOMContentLoaded", init);
let eb = null;

function init() {
    eb = new EventBus("http://" + window.location.host + "/socket/");
    setTimeout(function () {
        sendtoBus("Connect",JSON.parse("{\"message\":\"hello\"}"));
        document.querySelector("#submit").addEventListener("click",addQuestion)
    }, 1000);
}

function addQuestion(e) {
    e.preventDefault();
    let question = document.querySelector("#question").value;
    let answers = document.querySelector("#answers").value;
    let solution = document.querySelector("#solution").value;
    console.log(JSON.parse("{\"question\":\""+question+"\",\"answers\":\""+answers+"\",\"correct\":"+solution+"}"))
    sendtoBus("Question",JSON.parse("{\"question\":\""+question+"\",\"answers\":\""+answers+"\",\"correct\":"+solution+"}"));

}
function sendtoBus(type, content) {
    eb.publish("socket.handler",
        {user: name, type: type, content: content},
        function (err) {
            if (err) {
                console.log("err: " + JSON.stringify(err))
            }
        }
    );
}

function validateForm() {
    let x = document.forms["createQuiz"]["answers"].value;
    if (!(x.indexOf(',') > -1)) {
        alert("You need to provide more then one possible answer & answers must be separated by a comma");
        return false;
    }
}