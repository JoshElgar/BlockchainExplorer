$(document).ready(function () {
    
    $.ajax("http://localhost:8080/data/getchartdata", {
        success: function(data) {
            var chartData = [];
            chartData.push(data.numTx);
            chartData.push(data.numBlocks);
            createChart(chartData);
        },
        error: function(result) {
            //console.log(result);
            createChart(new Array(5, 6));
        }
    });

});


function createChart(chartData) {
    var ctx = $("#chart1");
    var myChart = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: ["Blocks", "Txs"],
            datasets: [{
                label: 'Total number',
                data: chartData,
                backgroundColor: [
                                        'rgba(255, 99, 132, 0.2)',
                                        'rgba(54, 162, 235, 0.2)',
                                        'rgba(255, 206, 86, 0.2)',
                                        'rgba(75, 192, 192, 0.2)',
                                        'rgba(153, 102, 255, 0.2)',
                                        'rgba(255, 159, 64, 0.2)'
                                    ],
                borderColor: [
                                        'rgba(255,99,132,1)',
                                        'rgba(54, 162, 235, 1)',
                                        'rgba(255, 206, 86, 1)',
                                        'rgba(75, 192, 192, 1)',
                                        'rgba(153, 102, 255, 1)',
                                        'rgba(255, 159, 64, 1)'
                                    ],
                borderWidth: 1
                                }]
        },
        options: {
            scales: {
                yAxes: [{
                    ticks: {
                        beginAtZero: true
                    }
                                    }]
            },
            maintainAspectRatio: false
        }
    });
}
