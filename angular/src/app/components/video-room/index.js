function displayRoomId(roomId) {
    var roomNum = document.getElementById('room-number');
    roomNum.innerHTML = roomId;
    roomNum.style.setProperty('display', 'block');
}

function displayUsersUl() {
    var select = document.getElementById('login-users');
    select.style.setProperty('display', 'block');
}

function hideButtonBar() {
    var buttonBar = document.getElementById('button-bar');
    buttonBar.style.setProperty('display', 'none');
}

function hideUsersUl() {
    var select = document.getElementById('login-users');
    select.style.setProperty('display', 'none');
}

function genNonDuplicateID(randomLength) {
    return Number(Math.random().toString().substr(3, randomLength) + Date.now()).toString(36);
}

function loginRoom() {
    var roomName = genNonDuplicateID();
    login(roomName);
    return roomName;
}

function createRoom() {
    var roomName = loginRoom();
    displayRoomId(roomName);
    hideButtonBar();
    hideUsersUl();
    window.alert(`Room Created!\nRoom ID:\n ${roomName}`);
}

function connectRoom() {
    login(genNonDuplicateID());
    displayUsersUl();
}

function overLi(evt) {
    var li = evt.target;
    li.style.setProperty('background-color', '#ffffff');
    li.style.setProperty('color', '#3f51b5');
    li.style.setProperty('font-weight', 'bold');
    li.style.setProperty('height', '120%');
}

function outLi(evt) {
    var li = evt.target;
    li.style.setProperty('background-color', '#3f51b5');
    li.style.setProperty('color', '#ffffff');
    li.style.setProperty('font-weight', 'normal');
    li.style.setProperty('height', '100%');
}
