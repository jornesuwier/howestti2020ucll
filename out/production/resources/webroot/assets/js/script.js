"use strict";
/* global EventBus, document, console, messagelist */
document.addEventListener("DOMContentLoaded", init);

let eb = null;
let name = "me";
let QuestionId;

function init() {
    eb = new EventBus("http://" + window.location.host + "/socket/");
    eb.onopen = function () {onOpen();};
    document.querySelector("#join").addEventListener("click",join);
}

function join(e) {
    e.preventDefault();
    name = document.querySelector("#name").value;
    sendtoBus("Connect",JSON.parse("{\"message\":\"hello\"}"));
    document.querySelector("#join").removeEventListener("click",join);
    document.body.innerHTML += "<button id=\"Start\">Start The Quiz</button>";
    document.querySelector("#Start").addEventListener("click",startQuiz);
}

function startQuiz() {
    sendtoBus("Start",JSON.parse("{\"message\":\"hello\"}"));
}

function answer(e) {
    e.preventDefault();
    document.querySelectorAll("#answers button").forEach(button => button.removeEventListener("click",answer))
    sendtoBus("Answer",JSON.parse("{\"answer\":\""+e.target.innerText+"\",\"questionId\":"+QuestionId+"}"));
}

function displayQuestion(json){
    document.querySelector("#question").innerHTML = json.question;
    const answers = json.answers;
    QuestionId = json.questionId;
    document.querySelector("#answers").innerHTML = "";
    for(let i=0;i<answers.length;i++){
        document.querySelector("#answers").innerHTML += "<button>"+answers[i]+"</button>";
    }
    document.querySelectorAll("#answers button").forEach(button => button.addEventListener("click",answer))
}


function sendtoBus(type,content) {
    eb.publish("socket.handler",
        {user: name, type: type, content: content},
        function (err, reply) {
            if (err) {
                console.log("err: " + JSON.stringify(err))
            }
        }
    );
}

function onOpen() {
    eb.registerHandler("socket.handler",
        function (error, message) {
            if (error) {
                console.log("error: " + JSON.stringify(error));
            } else {
                const data = message.body;
				const type = data.type;
				switch(type){
                    case "Connect":
						document.querySelector("#log").innerHTML += "<li>"+data.user+" has joined</li>";
						break;
                    case "Question":
                        displayQuestion(data);
                        break;
                    case "Score":
                        if(data.user === name){
                            document.querySelector("#score").innerHTML = data.score;
                        }
                        break;
                    case "Time":
                        document.querySelector("#time").innerHTML = data.time;
                        break;
                    case "End":
                        document.querySelector("#log").innerHTML += "<li>Quiz Ended</li>";
                        break;
					default:
						console.log(data);
				}
            }
        }
    );
}