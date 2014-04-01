(function() {

  'use strict';

  // Create the app and assign the DOM Element container.
  window.Canary = Ember.Application.create({
    rootElement:"#canary"
  });

  Canary.dateOffsetInMilliseconds = function (start, stop) {
    start = start || '';
    stop = stop || '';
    var startDate = new Date(start), stopDate = new Date(stop);
    return stopDate - startDate;
  };

})();