$(document).ready(function () {
    
    $.ajax("http://localhost:8080/data/getchartdata", {
        success: function(data) {
            createBarChart(data);
            createTimeChart(data);
        },
        error: function(result) {
            console.log(result)
        }
    });

});

function createTimeChart(data) {
    var chartData = [5, 6, 7, 8, 9, 10];
    //chartData.push(data[1].numTx);
    //chartData.push(data[1].numBlocks);
    
    
    var ctx = $("#chart2");
    var myChart = new Chart(ctx, {
        type: 'line',
        data: {
            labels: [new Date(2018, 01, 20), new Date(2018, 02, 20), new Date(2018, 03, 20), new Date(2018, 04, 20), new Date(2018, 05, 20), new Date(2018, 06, 20)],
            datasets: [{
                label: 'Transactions',
                data: chartData,
                backgroundColor: '#fe8b36',
                borderColor: '#fe8b36',
            }]
        },
        options: {
            scales: {
                xAxes: [{
                    type: 'time',
                    display: true,
                }],
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


function createBarChart(data) {
    var chartData = [];
    chartData.push(data[0].numTx);
    chartData.push(data[0].numBlocks);
    
    
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
