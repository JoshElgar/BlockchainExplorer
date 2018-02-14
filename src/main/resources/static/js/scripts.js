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
            processBlockMessage(JSON.parse(block.body));
        });
        stompClient.subscribe('/topic/tx', function (tx) {
            processTxMessage(JSON.parse(tx.body));
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

function updateLastBlockHeight() {
    stompClient.send("/app/socket/updateLastBlockHeight", {}, lastBlockHeight);                     
}

var lastTxSerial;

function updateLastTxSerial() {
    stompClient.send("/app/socket/updateLastTxSerial", {}, lastTxSerial);                     
}

//json blocks array
var blockList = new Array();

function processBlockMessage(message) {
//	message is an array of blocks
    blockList = [];
	
//	Add new blocks to global block array
	message.forEach(function(block) {
		blockList.unshift(block);
	});
    
    lastBlockHeight = blockList[blockList.length - 1].height;
    updateLastBlockHeight();

	//sortBlockList();
	
	updateLiveBlocks();
}

var txList = new Array();

function processTxMessage(message) {
    txList = [];
    
    message.forEach(function(tx) {
        txList.unshift(tx);
    });
    
    lastTxSerial = txList[txList.length - 1].serialid;
    updateLastTxSerial();
    
    updateLiveTx();
}

//sort in descending height (highest first)
function sortBlockList() {
	blockList.sort(function(a, b) {
		return parseInt(b.height) - parseInt(a.height);
	});
}

function updateLiveBlocks() {
	liveBlockTable = $("#liveBlockTable");
	
	//get 5 highest blocks
	var highestBlocks = blockList.slice(0, 5);
	
	highestBlocks.forEach(function(block) {
        var blockDate = new Date(block.time);
        
		var blockRow = "<tr><td>" + block.height + "</td><td>" + block.numTx + "</td><td>" + blockDate.toUTCString + "</td></tr>";
        liveBlockTable.first("tbody").prepend(blockRow);
    });
    
    //one tr is the header row
    var n = liveBlockTable.find("tr").length - 6;
    if (n > 0) {
        //remove last N <tr> elements
        liveBlockTable.find("tr").slice(-n).remove();
    }
}

function updateLiveTx() {
    liveTxTable = $("#liveTxTable");
    
    //get 10 highest tx
    var highestTxs = txList.slice(0, 10);
    
    highestTxs.forEach(function(tx) {
        var txRow = "<tr><td>" + tx.hash + "</td></tr>";
        liveTxTable.first("tbody").prepend(txRow);
    });
    
    //one tr is the header row
    var n = liveTxTable.find("tr").length - 11;
    if (n > 0) {
        //remove last N <tr> elements
        liveTxTable.find("tr").slice(-n).remove();
    }
}