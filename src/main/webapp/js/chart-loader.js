(function(){
  var drawChart = function() {
      var diffTypeOfTea = new google.visualization.DataTable();
      //define columns for the DataTable instance
      diffTypeOfTea.addColumn("string", "Types of Tea");
      diffTypeOfTea.addColumn("number", "Cupes Of Tea");
      //add temp data to diffTypeOfTea chart
      diffTypeOfTea.addRows([
        ["Green Tea", 6],
        ["Black Tea", 10],
        ["Oolong Tea", 7],
        ["Matcha Tea", 4],
        ["White Tea", 8]
      ]);
      //cutomization to the chart
      var chartOptions = {
        'title': 'Different Types of Tea We Drink in a Day',
        width: 800,
        height: 400
      };
      var chart = new google.visualization.BarChart(
        document.getElementById("diffTeaChart")
      );
      chart.draw(diffTypeOfTea, chartOptions);
  };

  var init = function() {
    google.charts.load("current", {packages: ["corechart"],});
    google.charts.setOnLoadCallback(
      function() {
        drawChart();
      });
  };

  init();
})();
