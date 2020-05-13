"use strict";
document.addEventListener("DOMContentLoaded", init);
function init() {
    document.querySelector("#play").addEventListener("click",join);
}
function join(e) {
    e.preventDefault();
    const name = document.querySelector("#Name").value;
    if(name !== "") {
        localStorage.setItem("name",name);
        window.location.href = "/game.html";
    }
}