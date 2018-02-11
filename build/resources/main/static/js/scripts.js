$(document).ready(function () {


    $(".portalControls").find(".syncButton").click(triggerSync);
    //connect();
    
});

function triggerSync(e) {
    e.preventDefault();
    $.ajax({
        type: "GET",
        url: "http://localhost:8080/testportal/sync",
        dataType: "text",
        success: writeToConsole,
        error: writeToConsole
    });
}

function writeToConsole(consoleText) {
    $(".portalConsole").text(consoleText);
}


/*
    Browser STOMP client (with SockJS)
*/

var stompClient = null;

var error_callback = function(error) {
    console.log(error);
}

function connect() {
    var socket = new WebSocket('ws://localhost:8080/testportal/stomptest');
    stompClient = Stomp.over(socket);
    
    stompClient.connect({}, function (frame) {
        //setConnected(true);
        console.log('Connected: \n' + frame);
        stompClient.subscribe('/topic/blocks', function (block) {
            processMessage(JSON.parse(block.body));
        });
    }, error_callback);
}
                        

function disconnect() {
    if (stompClient != null) {
        stompClient.disconnect();
    }
    //setConnected(false);
    console.log("Disconnected");
}
/*
function sendMessage() {
    var from = document.getElementById('from').value;
    var text = document.getElementById('text').value;
    stompClient.send("/app/stomptest", {},
        JSON.stringify({
            'from': from,
            'text': text
        }));
}
*/

//json blocks array
var blockList = new Array();

function processMessage(message) {
//	message is an array of blocks
    blockList = [];
	
//	Add new blocks to global block array
	message.forEach(function(block) {
		blockList.push(block);
	});

	sortBlockList();
	
	updateLiveBlocks();
}

//sort in descending height (highest first)
function sortBlockList() {
	blockList.sort(function(a, b) {
		return parseInt(b.height) - parseInt(a.height);
	});
}

//live block element array
var liveBlocks = new Array();

function updateLiveBlocks() {
	liveBlocksTable = $("#latestBlockTable");
	
	sortLiveBlocks();
	
	
	//get 5 highest blocks
	var highestBlocks = blockList.slice(0, 5);
	
	highestBlocks.forEach(function(block) {
		console.log("block: " + block);
		var blockRow = "<tr><td>" + block.height + "</td><td>" + block.numTx + "</td><td>" + block.time + "</td></tr>";
        liveBlocksTable.first("tbody").append(blockRow);
    });
    
    var n = liveBlocksTable.find("div").length - 5;
    if (n > 0) {
        liveBlocksTable.slice(-n).remove();
    }
}

function sortLiveBlocks(){
	liveBlocks.sort(function(a, b) {
		var contentA = parseInt( $(a).attr('data-height'));
	    var contentB = parseInt( $(b).attr('data-height'));
	    return (contentA < contentB) ? 1 : (contentA > contentB) ? -1 : 0;
	});
	
}



function showMessageOutput(messageOutput) {
    var console = document.getElementById('console');
    messageOutput.forEach(function(block) {
	    var p = document.createElement('p');
	    p.style.wordWrap = 'break-word';
	    p.appendChild(document.createTextNode("Block " + block.height + ": " + block.time));
	    console.appendChild(p);
    });
}
