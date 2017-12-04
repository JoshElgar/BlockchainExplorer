$(document).ready(function () {


    $(".portalControls").find(".syncButton").click(triggerSync);

});

function triggerSync(e) {
    e.preventDefault();
    $.ajax({
        type: "GET",
        url: "/testportal/sync",
        dataType: "text",
        success: writeToConsole,
        error: writeToConsole
    });
}

function writeToConsole(consoleText) {
    $(".portalConsole").text(consoleText);
}