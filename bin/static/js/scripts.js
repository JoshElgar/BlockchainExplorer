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
    var socket = new WebSocket('ws://localhost:8080/socket');
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

var lastBlockHeight;

function updateLastBlock() {
    stompClient.send("/app/socket/updateLastBlock", {}, lastBlockHeight);
        //JSON.stringify({'lastBlockHeight': 508833,}));
                     
}


//json blocks array
var blockList = new Array();

function processMessage(message) {
//	message is an array of blocks
    blockList = [];
	
//	Add new blocks to global block array
	message.forEach(function(block) {
		blockList.unshift(block);
	});
    
    lastBlockHeight = blockList[blockList.length - 1].height;
    updateLastBlock();

	//sortBlockList();
	
	updateLiveBlocks();
}

//sort in descending height (highest first)
function sortBlockList() {
	blockList.sort(function(a, b) {
		return parseInt(b.height) - parseInt(a.height);
	});
}

function updateLiveBlocks() {
	liveBlocksTable = $("#latestBlockTable");
	
	//get 5 highest blocks
	var highestBlocks = blockList.slice(0, 5);
	
	highestBlocks.forEach(function(block) {
		var blockRow = "<tr><td>" + block.height + "</td><td>" + block.numTx + "</td><td>" + block.time + "</td></tr>";
        liveBlocksTable.first("tbody").prepend(blockRow);
    });
    
    //one tr is the header row
    var n = liveBlocksTable.find("tr").length - 6;
    if (n > 0) {
        //remove last N <tr> elements
        liveBlocksTable.find("tr").slice(-n).remove();
    }
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
