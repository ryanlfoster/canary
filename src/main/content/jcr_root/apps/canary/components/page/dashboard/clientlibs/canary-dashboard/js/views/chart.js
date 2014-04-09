(function() {

  'use strict';


  /** This object holds the configration for the ChartJS output. */
  var chartJSConfig = {

    scaleOverlay : false, //Boolean - If we show the scale above the chart data

    scaleOverride : false, //Boolean - If we want to override with a hard coded scale

    //** Required if scaleOverride is true **
    scaleSteps : null, //Number - The number of steps in a hard coded scale
    scaleStepWidth : null, //Number - The value jump in the hard coded scale
    scaleStartValue : null, //Number - The scale starting value

    scaleLineColor : "rgba(0,0,0,.1)", //String - Colour of the scale line

    scaleLineWidth : 1, //Number - Pixel width of the scale line

    scaleShowLabels : true, //Boolean - Whether to show labels on the scale

    scaleLabel : "<%=value%>", //Interpolated JS string - can access value

    scaleFontFamily : "'Arial'", //String - Scale label font declaration for the scale label

    scaleFontSize : 12, //Number - Scale label font size in pixels

    scaleFontStyle : "normal", //String - Scale label font weight style

    scaleFontColor : "#666", //String - Scale label font colour

    scaleShowGridLines : true, //Boolean - Whether grid lines are shown across the chart

    scaleGridLineColor : "rgba(0,0,0,.05)", //String - Colour of the grid lines

    scaleGridLineWidth : 1, //Number - Width of the grid lines

    bezierCurve : true, //Boolean - Whether the line is curved between points

    pointDot : true, //Boolean - Whether to show a dot for each point

    pointDotRadius : 3, //Number - Radius of each point dot in pixels

    pointDotStrokeWidth : 1, //Number - Pixel width of point dot stroke

    datasetStroke : true, //Boolean - Whether to show a stroke for datasets

    datasetStrokeWidth : 2, //Number - Pixel width of dataset stroke

    datasetFill : true, //Boolean - Whether to fill the dataset with a colour

    animation : true, //Boolean - Whether to animate the chart

    animationSteps : 60, //Number - Number of animation steps

    animationEasing : "easeOutQuart", //String - Animation easing effect

    onAnimationComplete : null //Function - Fires when the animation is complete

  };




  Canary.ChartView = Ember.View.extend({

    didInsertElement: function() {
      /** Avoid asynchronicity issues by calling this once directly. */
      this.drawLineChart();
    },

    // Properties:

    hideLabels: false,

    /** Draw line chart */
    lineChartBuilder: function() {

      /** Cache a reference the jQuery object representing the chart container. */
      var $chartContainer = this.$(".chart");

      /** Build the chart data object without labels and data points. */
      var chartData = {labels: [], datasets: [{
        fillColor : "rgba(151,187,205,0.5)",
        strokeColor : "rgba(151,187,205,1)",
        pointColor : "rgba(151,187,205,1)",
        pointStrokeColor : "#fff",
        data: []
      }]};

      /** If there are records, loop over them and put the labels and datapoints into the chart data object. */
      if ( this.get('controller.records.records').length > 0 ) {
        _.forEach(this.get('controller.records.records'), function(record) {
          chartData.labels.push(record.startTime);
          chartData.datasets[0].data.push(Canary.dateOffsetInMilliseconds(record.startTime, record.endTime));
        });
      }

      /** If there is a jQuery onject to put the output in and some data in the chart data object ... */
      if ($chartContainer && chartData.labels.length > 0) {

        /** Set the width and the height of the chart container to support responsive layout. */
        $chartContainer.attr('width', $chartContainer.parent().width());
        $chartContainer.attr('height', $chartContainer.parent().width() / 1.6);

        /** Set the getContext value for the canvas element. */
        var chartContext = $chartContainer.get(0).getContext("2d");

        /** Hide the labels if the controller says to. */
        if ( this.get( 'hideLabels' ) === true ) {
          var numLabels = chartData.labels.length;
          var newLabels = [];
          for (var x=0; x<numLabels; x++) {
            newLabels.push('');
          }
          chartData.labels = newLabels;
        }

        /** Create a new Line Chart using Chart.js. */
        Canary.recordChart = new Chart(chartContext).Line(chartData, chartJSConfig);

      }

    },

    /**
     * Observes another observer of the chart data.
     * Observing the chart data directly doesn't seem to work.
     * Debouncing the call to the actual chartbuilder method,
     * because this observer fires for each datapoint.
     */
    drawLineChart: function() {
      Ember.run.debounce(this, this.lineChartBuilder, 100);
    }.observes('controller.average')

  });

})();