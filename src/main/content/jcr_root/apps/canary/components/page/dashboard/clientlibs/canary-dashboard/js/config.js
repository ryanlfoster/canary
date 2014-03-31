(function(){

  'use strict';

  Canary.config = {

    // Configuration for Chart.JS Line Chart output. http://www.chartjs.org/docs/#gettingStarted-creatingAChart
    chartJSLineChartOptions : {

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

    }

  };

  Ember.Handlebars.helper('duration', function(startTime, stopTime) {
    return new Handlebars.SafeString(Canary.dateOffsetInMilliseconds(startTime, stopTime));
  });

})();