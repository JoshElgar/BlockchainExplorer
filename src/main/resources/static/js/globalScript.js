$(document).ready(function () {


    $("#searchFormButton").click(function () {

        console.log("Submitting search for: " + $("#searchFormInput").val());

        var url = "http://localhost:8080/search/".concat($("#searchFormInput").val());

        $.ajax(url, {
            success: function (data) {
                console.log("Found: " + data);
                if (data === "none") {
                    alert("Couldn't find any matching blocks/txs.");
                } else if (data === "block") {
                    window.location.replace("http://localhost:8080/block/" + data.hash);
                } else if (data === "tx") {
                    window.location.replace("http://localhost:8080/tx/" + data.txid);
                }
            },
            error: function (result) {
                console.log(result);
            }
        });
    });
});
