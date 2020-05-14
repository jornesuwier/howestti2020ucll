"use strict";
/* global EventBus, document, console, messagelist */
document.addEventListener("DOMContentLoaded", init);

let eb = null;
let name = localStorage.getItem("name");
let GID = localStorage.getItem("GID");
let QuestionId;

function init() {
    eb = new EventBus("http://" + window.location.host + "/socket/");
    eb.onopen = function () {
        onOpen();
    };
    setTimeout(function () {
        sendtoBus("Connect", JSON.parse("{\"message\":\"hello\"}"));
        document.querySelector("#createRoom").addEventListener("click", createRoom);
        document.querySelector("#joinRoomSubmit").addEventListener("click", joinRoom)
    }, 1000);
}

function createRoom(e) {
    e.preventDefault();
    GID = createGID();
    document.querySelector("#generatedCode").innerHTML = GID;
    localStorage.setItem("GID", GID);
    sendtoBus("Create", JSON.parse("{\"message\":\"hello\"}"));
    document.querySelector("#startQuiz").classList.remove("hidden");
    document.querySelector("#startQuiz").addEventListener("click", startQuiz);
    setTimeout(function () {
        hideRooms();
    }, 2000)

}

function startQuiz(e) {
    e.preventDefault();
    sendtoBus("Start", JSON.parse("{\"message\":\"hello\"}"));
}

function joinRoom(e) {
    e.preventDefault();
    GID = document.querySelector("#roomCode").value;
    localStorage.setItem("GID", GID);
    sendtoBus("Join", JSON.parse("{\"message\":\"hello\"}"));
    document.querySelector("#gid").innerHTML = GID;
    document.querySelector("#player").innerHTML = name;
    hideRooms();
}

function hideRooms() {
    document.querySelector("#roomConfig").classList.add("hidden");


}

function answer(e) {
    e.preventDefault();
    e.currentTarget.classList.add("selected");
    document.querySelectorAll(".choice-container").forEach(button => button.removeEventListener("click", answer));
    sendtoBus("Answer", JSON.parse("{\"answer\":\"" + e.currentTarget.children[1].innerHTML + "\",\"questionId\":" + QuestionId + "}"));
}

function displayQuestion(json) {
    document.querySelector("#question").innerHTML = json.question;
    const answers = json.answers;
    QuestionId = json.questionId;
    document.querySelector("#answers").innerHTML = "";
    for (let i = 0; i < answers.length; i++) {
        document.querySelector("#answers").innerHTML += '<div class="choice-container"><p class="choice-prefix">' + (i + 10).toString(36).toUpperCase() + '</p><p class="choice-text">' + answers[i] + '</p></div>';
    }
    document.querySelectorAll(".choice-container").forEach(button => button.addEventListener("click", answer))
}

function sendtoBus(type, content) {
    eb.publish("socket.handler",
        {GID: GID, user: name, type: type, content: content},
        function (err) {
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
                switch (type) {
                    case "Question":
                        displayQuestion(data);
                        removeStartButton();
                        break;
                    case "Score":
                        if (data.user === name) {
                            document.querySelector("#score").innerHTML = data.score;
                        }
                        removeStartButton();
                        break;
                    case "Time":
                        document.querySelector("#time").innerHTML = data.time;
                        removeStartButton();
                        break;
                    case "Players":
                        document.querySelector("#players").innerHTML = data.players;
                        break;
                    case "Start":
                        removeStartButton();
                        break;
                    case "End":
                        document.querySelector("#endscore").innerHTML = "Scoreboard";
                        document.querySelector("#answers").innerHTML = "";
                        document.querySelector("#question").innerHTML = "";
                        removeStartButton();
                        //addEndButton();
                        //document.querySelector("#end").addEventListener("click", goToStart);
                        break;
                    case "loginReply":
                        console.log(type + "//  " + data);
                        document.querySelector("#player").innerHTML = name;
                        GID = data.GID;
                        document.querySelector("#gid").innerHTML = GID;
                        document.querySelector("#generatedCode").innerHTML = GID;
                        break;
                    case "Scoreboard":
                        let output = "";
                        data.playerscores.forEach(ps => {
                            output = `<div><ul><li>${ps.user}</li><li>${ps.score} points</li></ul></div>`;
                            document.querySelector("#scoreboard").innerHTML += output;
                        });

                        console.log(data);
                        break;
                    default:
                        break;
                }
            }
        }
    );
}

function removeStartButton() {
    const btn = document.querySelector("#start");
    if (btn !== null) {
        document.querySelector("#game").removeChild(btn);
    }
}

function addEndButton() {
    document.querySelector("#end").removeAttribute("hidden");
}

function goToStart() {
    window.location.href = "/"
}

//unique identifier function
function createGID() {
    let dt = new Date().getTime();
    let gid = 'xxxx-xxxx'.replace(/[xy]/g, function (c) {
        let r = (dt + Math.random() * 16) % 16 | 0;
        dt = Math.floor(dt / 16);
        return (c == 'x' ? r : (r & 0x3 | 0x8)).toString(16);
    });
    return gid;
}
