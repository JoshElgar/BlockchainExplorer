$(document).ready(function () {

    $(function () {
        $('[data-toggle="tooltip"]').tooltip()
    })


    $("#searchFormButton").click(function () {

        console.log("Submitting search for: " + $("#searchFormInput").val());

        var url = "http://localhost:8080/api/search/".concat($("#searchFormInput").val());

        $.ajax(url, {
            success: function (data) {
                console.log("Found: " + data.constructor.name);
                if ('none' in data) {
                    alert("Couldn't find any matching blocks/txs.");
                } else if ('block' in data) {
                    window.location.replace("http://localhost:8080/api/block/" + data.block);
                } else if ('tx' in data) {
                    window.location.replace("http://localhost:8080/api/tx/" + data.tx);
                }
            },
            error: function (result) {
                console.log(result);
            }
        });
    });
});
