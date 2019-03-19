(function() {

  var _messageChartPromise = null;

  var drawBarChart = function() {
    var diffCaffeine = new google.visualization.DataTable();
    //define columns for the DataTable instance
    diffCaffeine.addColumn("string", "Days of the Week");
    diffCaffeine.addColumn("number", "mg of Caffeine");
    //add temp data to diffCaffeine chart
    diffCaffeine.addRows([
      ["Monday", 600],
      ["Tuesday", 100],
      ["Wednesday", 800],
      ["Thursday", 700],
      ["Friday", 40]
    ]);
    //cutomization to the chart
    var chartOptions = {
      'title': 'Amount of Caffeine Consumed in a Day',
      width: 800,
      height: 400
    };
    var chart = new google.visualization.BarChart(
      document.getElementById("caffeine_chart")
    );
    chart.draw(diffCaffeine, chartOptions);
  };

  var drawColumnChart = function() {
    var diffTeaTypes = new google.visualization.DataTable();

    diffTeaTypes.addColumn("string", "Types of Tea");
    diffTeaTypes.addColumn("number", "Cups Of Tea");

    diffTeaTypes.addRows([
      ["Green Tea", 6],
      ["Black Tea", 10],
      ["Oolong Tea", 7],
      ["Matcha Tea", 4],
      ["White Tea", 8]
    ]);

    var columnOptions = {
      title: 'Amount of Tea in a Given Week',
      width: 600,
      height: 400,
      bar: {
        groupWidth: "95%"
      },
      legend: {
        position: "none"
      },
    };

    var chart = new google.visualization.ColumnChart(
      document.getElementById("tea_chart")
    );
    chart.draw(diffTeaTypes, columnOptions);
  };

  var drawPieChart = function() {
    var diffFavoriteTea = new google.visualization.DataTable();
    diffFavoriteTea.addColumn('string', 'Tea');
    diffFavoriteTea.addColumn('number', 'Populartiy');

    diffFavoriteTea.addRows([
      ["Green Tea", 46],
      ["Black Tea", 7],
      ["Oolong Tea", 3],
      ["Matcha Tea", 8],
      ["White Tea", 4],
      ["Boba Tea", 32]
    ]);

    var pieOptions = {
      title: 'Popularity of Types of Tea',
      width: 900,
      height: 500
    };
    var chart = new google.visualization.PieChart(document.getElementById('pie_chart'));
    chart.draw(diffFavoriteTea, pieOptions);
  };

  function fetchMessageData() {
    const url = "/messagechart";
    if(_messageChartPromise == null) {
      _messageChartPromise = fetch(url)
      .then((response) => { 
        return response.json();
      })
      .then((msgJson) => {
        console.log(msgJson);
      });
    }
    return _messageChartPromise;
  }

  var init = function() {
    google.charts.load("current", {
      packages: ["corechart"],
    });
    google.charts.setOnLoadCallback(
      function() {
        fetchMessageData();
        drawBarChart();
        drawColumnChart();
        drawPieChart();
      });
  };

  init();
})();
