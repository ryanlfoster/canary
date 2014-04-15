(function() {

  'use strict';



  /** Create the app and assign the DOM Element container. */
  window.Canary = Ember.Application.create({
    rootElement: "#canary"
  });



  /**
   * Given a date formatted string like "2014-04-07T13:52:25.861-0400", create a date object.
   * @param {string} dateString A date string-formatted string.
   * @returns {object} a Date object.
   */
  function dateFromDateString(dateString) {
    var date, dateArray = dateString.split(/[^0-9]/);
    if (dateArray.length < 7) {
      throw 'Invalid Date String: ['+dateString+']';
    } else {
      date = new Date(dateArray[0], dateArray[1]-1, dateArray[2], dateArray[3], dateArray[4], dateArray[5], dateArray[6]);
    }
    return date;
  }



  /**
   * Given nothing, a date string, or a date object, return a date object.
   * Does not validate that the date string is valid or the object is in fact a date.
   * If param is an empty string or undefined, returns the current date.
   * @param {string or object} param A date string or a date object is optional.
   * @returns {object} a Date object.
   */
  function generateDate(param) {
    var resultDate;

     /** default the parameters to null */
     /** If the parameters are strings, assume date strings and make date objects. Otherwise, assume date objects. */
    if (param === '' || typeof param === 'undefined') {
      param = new Date();
    } else if ( _.isDate(param) === true ) {
      resultDate = param;
    } else if (typeof param === 'string') {
      resultDate = dateFromDateString(param);
    } else {
      throw 'Date Param Type Invalid';
    }

    return resultDate;
  }



  /**
   * This function is used throughout the application to calculate the offset in milliseconds between two date objects.
   * @param {string or object} start The first date object or a date string to generate the first date object from.
   * @param {string or object} stop The second date object or a date string to generate the second date object from.
   * @returns {number}
   */
  Canary.dateOffsetInMilliseconds = function (start, stop) {
    var startDate, stopDate, offset = 0;

    /** Wrap the calculate in a try...catch in case the parameters are not date objects. */
    try {
      startDate = generateDate(start);
      stopDate = generateDate(stop);

      if ( startDate.valueOf === 'undefined' ) {
        throw "Undefined Date.";
      }
      if ( isNaN(startDate.valueOf()) === true || isNaN(stopDate.valueOf()) === true ) {
        throw "Invalid Date.";
      }
      offset = stopDate.valueOf() - startDate.valueOf();
    } catch(err) {
      console.error('An error occured calculating the offset between two dates. The parameters did not generate valid date objects. Error: '+err);
    }

    /** return the offset. */
    return offset;
  };



  /** One handlebars helper: make dateOffsetInMilliseconds accessible from within templates. */
  Ember.Handlebars.helper('duration', function(startTime, stopTime) {
    return new Handlebars.SafeString(Canary.dateOffsetInMilliseconds(startTime, stopTime));
  });



})();