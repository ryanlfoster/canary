(function() {

  'use strict';

  Canary.ChartView = Ember.View.extend({

    didInsertElement: function() {
      // Need to grab a calculated value or that value will never get calculated
      // and observers will never fire.
      this.get('controller.records.length');

      // Avoid asynchronicity issues by calling this once directly.
      this.drawLineChart();
    },

    // Properties:

    hideLabels: false,

    // Methods:

    // Draw line chart
    lineChartBuilder: function() {

      var chartData = {labels: [], datasets: [{
        fillColor : "rgba(151,187,205,0.5)",
        strokeColor : "rgba(151,187,205,1)",
        pointColor : "rgba(151,187,205,1)",
        pointStrokeColor : "#fff",
        data: []
      }]};

      if ( this.get('controller.records.records').length > 0 ) {
        _.forEach(this.get('controller.records.records'), function(record) {
          chartData.labels.push(record.startTime);
          chartData.datasets[0].data.push(Canary.dateOffsetInMilliseconds(record.startTime, record.endTime));
        });
      }

      // Cache a reference the jQuery object representing the chart container.
      var $chartContainer = this.$(".chart");

      if ($chartContainer && chartData.labels.length > 0) {

        // Set the width and the height of the chart container to support responsive layout.
        $chartContainer.attr('width', $chartContainer.parent().width());
        $chartContainer.attr('height', $chartContainer.parent().width() / 1.6);

        // Set the getContext value for the canvas element.
        var chartContext = $chartContainer.get(0).getContext("2d");

        // Hide the labels if the controller says to.
        if ( this.get( 'hideLabels' ) === true ) {
          var numLabels = chartData.labels.length;
          var newLabels = [];
          for (var x=0; x<numLabels; x++) {
            newLabels.push('');
          }
          chartData.labels = newLabels;
        }

        // Create a new Line Chart using Chart.js.
        Canary.recordChart = new Chart(chartContext).Line(chartData, Canary.config.chartJSLineChartOptions);

      }

    },

    // This method observes another observer of the chart data.
    // Observing the chart data directly doesn't seem to work.
    // Debouncing the call to the actual chartbuilder method,
    // because this observer fires for each datapoint.
    drawLineChart: function() {
      Ember.run.debounce(this, this.lineChartBuilder, 100);
    }.observes('controller.average')

  });

})();