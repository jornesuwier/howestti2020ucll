"use strict";
document.addEventListener("DOMContentLoaded", init);

function init() {
    document.querySelector("#joinRoom").addEventListener('click', toggle);
    document.querySelector("#createRoom").addEventListener('click', toggle);
}

function toggle(e) {
    let id = e.target.id;
    let joinroom = document.querySelector("#join");
    let createroom = document.querySelector("#create");

    switch (id) {
        case 'joinRoom':
            joinroom.classList.remove("hidden");
            createroom.classList.add("hidden");
            break;
        case 'createRoom':
            joinroom.classList.add("hidden");
            createroom.classList.remove("hidden");
            break;
        default:
            joinroom.classList.add("hidden");
            createroom.classList.add("hidden");
            break;
    }

}