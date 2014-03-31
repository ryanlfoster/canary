(function() {

  'use strict';

  /** canaryData is the object that stores the dashboard data. */
  var canaryData = {},
      /** Valid typeKeys for data requets. */
      typeKeys = ['MONITOR', 'NOTIFICATION_AGENT', 'POLL_RESPONSE_HANDLER', 'RECORD_PERSISTENCE_SERVICE'],

      /** The paths used for AJAX requests to get data. */
      paths = {

        /** list paths */
        MONITOR: 'etc/canarydashboard/jcr:content.listmonitors.json',
        NOTIFICATION_AGENT: 'etc/canarydashboard/jcr:content.listnotificationagents.json',
        POLL_RESPONSE_HANDLER: 'etc/canarydashboard/jcr:content.listpollresponsehandlers.json',
        RECORD_PERSISTENCE_SERVICE: 'etc/canarydashboard/jcr:content.listrecordpersistenceservices.json',

        /**
         * where identifier is the identifier listed in a call to list notification agents, poll response handlers, or record persistence services
         * where type is NOTIFICATION_AGENT or POLL_RESPONSE_HANDLER or RECORD_PERSISTENCE_SERVICE - ?identifier=&type=
         */
        statistics: 'etc/canarydashboard/jcr:content.statistics.json',

        /** where identifier is the identifier listed in list monitors - ?identifier=*/
        records: 'etc/canarydashboard/jcr:content.records.json'

      };



  /**
   * Determine if a given typeKey value is valid (exists in the typeKeys array).
   * @param {string} typeKey The typeKey value to check against the typeKeys array.
   * @returns {boolean}
   */
  function isValidTypeKey ( typeKey ) {
    return ( typeKeys.indexOf( typeKey ) > -1 );
  }



  /**
   * Return the path for the AJAX call for a list of items based on a given typeKey value.
   * @param {string} typeKey The typeKey value for the type of item to get the list path for.
   * @returns {string}
   */
  function listPath ( typeKey ) {
    if ( isValidTypeKey( typeKey ) ) {
      return paths[ typeKey ];
    }
    return undefined;
  }



  /**
   * Return the path for the AJAX call for the statistics of an item based on a given typeKey and identifier value.
   * Note: statistics call applies to 'NOTIFICATION_AGENT', 'POLL_RESPONSE_HANDLER' and 'RECORD_PERSISTENCE_SERVICE' only.
   * @param {string} id The identifier for the item to get the statistics path for.
   * @param {string} typeKey The typeKey value for the type of item to get the statistics path for.
   * @returns {string}
   */
  function statisticsPath( typeKey, id ) {
    if ( isValidTypeKey( typeKey ) && typeKey !== 'MONITOR' ) {
      return paths[ 'statistics' ] + '?identifier=' + id + '&type=' & typeKey;
    }
    return undefined;
  }



  /**
   * Return the path for the AJAX call for the records of a given MONITOR.
   * Note: records applies to a given MONITOR only. This function validates the typeKey to avoid asking for records for an item of the other types.
   * @param {string} id The identifier for the item to get the records path for.
   * @param {string} typeKey The typeKey value for the type of item to get the records path for (only MONITOR will work).
   * @returns {string}
   */
  function recordsPath( typeKey, id ) {
    if ( typeKey === 'MONITOR' ) {
      return paths[ 'records' ] + '?identifier=' + id;
    }
    return undefined;
  }



  /**
   * Converts a given pollInterval and pollIntervalUnit for a MONITOR object into a number of milliseconds.
   * Note: returns 0 if either value is invalid.
   * @param {number} pollInterval The monitor's poll interval (whole number of units specified by pollIntervalUnit).
   * @param {string} pollIntervalUnit A Java TimeUnit Enum value associated with the monitor's pollInterval.
   * @returns {number}
   */
  function convertPollIntervalToInteger (pollInterval, pollIntervalUnit) {
    var multiplier = 0;
    switch(pollIntervalUnit) {
      case 'DAYS':
        multiplier = 24 * 60 * 60 * 1000;
        break;
      case 'HOURS':
        multiplier = 60 * 60 * 1000;
        break;
      case 'MICROSECONDS':
        multiplier = 1 / 1000;
        break;
      case 'MILLISECONDS':
        multiplier = 1;
        break;
      case 'MINUTES':
        multiplier = 60 * 1000;
        break;
      case 'NANOSECONDS':
        multiplier = (1 / 1000) * (1 / 1000);
        break;
      case 'SECONDS':
        multiplier = 1000;
        break;
      default:
        /** If pollIntervalUnit isn't one of the recognized values, just return 0. */
        multiplier = 0;
    }

    /** If pollInterval is invalid, just return 0. */
    if (typeof pollInterval !== 'number') {
      return 0;
    }

    return pollInterval * multiplier;
  }



  /**
   * Determines whether a base date is greater than (later, occurs after) a compare date by more than a given offset (in milliseconds).
   * @param {object} baseDate The base date to compare (return true if later than compare date by more than offset).
   * @param {object} compareDate The compare date to compare (return true if earlier than base date by more than offset).
   * @param {number} [offsetMilliseconds=0]  The number of milliseconds to add to subtract from the base date.
   * @returns {boolean}
   */
  function compareDatesWithOffset (baseDate, compareDate, offsetMilliseconds) {
    return ( (baseDate - offsetMilliseconds ) > compareDate );
  }



  /**
   * Determines whether a given monitor or list of monitors need to have thier records refreshed based upon thier pollInterval.
   * @param {object} monitor A single monitor to check or a list of monitors to check.
   * @returns {boolean}
   */
  function ifMonitorsNeedRefreshed ( monitor ) {
    var now = new Date(), needToRefresh = false;
    if (typeof monitor.pollInterval === 'number' && typeof monitor.pollIntervalUnit === 'string' ) {
      needToRefresh = compareDatesWithOffset(now, monitor.recordLookUpDate, convertPollIntervalToInteger(monitor.pollInterval, monitor.pollIntervalUnit));
    } else if (typeof monitor === 'undefined') {
      _.forEach(monitor, function ( mon ) {
        needToRefresh = needToRefresh || compareDatesWithOffset(now, mon.recordLookUpDate, convertPollIntervalToInteger(mon.pollInterval, mon.pollIntervalUnit));
      });
    }
    return needToRefresh;
  }



  /**
   * Loads the list of items of a given type, then calls loadItemData() to load associated statistics or records, and returns a promise.
   * @param {string} typeKey The typeKey value for the type of items to load and return.
   * @param {string} id The identifier for the item to return is optional.
   * @returns {object} Promise
   */
  function load ( typeKey ) {
    return $.getJSON( listPath( typeKey ), function(response) {
      setAll( typeKey, response );
    }).then(function() {
      return loadItemData( typeKey );
    });
  }



  /**
   * Loads either the statistics or records for the specified type of existing items then returns the promise.
   * @param {string} typeKey The typeKey value for the type of items to get associated data for.
   * @returns {object} Promise
   */
  function loadItemData ( typeKey ) {
    var requestPath,
        dataType,
        doneCount = 0,
        doneTarget = canaryData[ typeKey ].length;

    /** Set the dataType value and requestPath interface based on the typeKey. */
    if ( typeKey === 'MONITOR' ) {
      dataType = 'records';
      requestPath = recordsPath;
    } else {
      dataType = 'statistics';
      requestPath = statisticsPath;
    }

    var promise = new Ember.RSVP.Promise(function(resolve) {

      /** For each item in the specified collection, make a call off to the appropriate request path and store the results, then call complete(). */
      _.forEach( canaryData[ typeKey ], function (item) {
        $.getJSON( requestPath( typeKey, item.identifier ), function( data ) {
          setData( typeKey, item.identifier, dataType, data );

          doneCount++;

          if (doneCount === doneTarget) {
            resolve( canaryData[ typeKey ] );
          }

        });
      });

    });

    /** return the promise */
    return promise;
  }



  /**
   * Getter for data already loaded to memory. Will return an individual record if an ID is specified, the entire list of specified type if not.
   * Note: Returns undefined if data doesn't exist yet.
   * @param {string} typeKey The typeKey for the record to return.
   * @param {string} id The identifyer for the record to return is optional.
   * @returns {object} Promise
   */
  function get ( typeKey, id ) {
    if ( typeof canaryData[ typeKey ] !== 'undefined' ) {
      if (typeof id !== 'undefined' ) {
        var item;
        for (var n=0; n<canaryData[ typeKey ].length; n++) {
          if ( canaryData[ typeKey ][ n ].identifier === id ) {
            item = canaryData[ typeKey ][ n ];
          }
        }
        return item;
      } else {
        return canaryData[ typeKey ];
      }
    }
    return undefined;
  }



  /**
   * Setter for entire list of items, but not associated records/statistics.
   * @param {string} typeKey The typeKey for the record to return.
   * @param {object} data The data to store.
   */
  function setAll (typeKey, data) {
    if ( isValidTypeKey( typeKey ) ) {
      canaryData[ typeKey ] = data;
    }
  }



  /**
   * Setter for records/statistics associated with an item.
   * @param {string} typeKey The typeKey for the record to associate data with.
   * @param {string} id The identifier for the record to associate data with.
   * @param {string} dataType The dataType (key) for the data.
   * @param {string} data The data to store.
   */
  function setData (typeKey, id, dataType, data) {
    if ( isValidTypeKey( typeKey ) ) {
      for (var n=0; n<canaryData[ typeKey ].length; n++) {
        if (canaryData[ typeKey ][ n ].identifier === id) {
          canaryData[ typeKey ][ n ][ dataType ] = data;
          if ( typeKey === 'MONITOR' &&  dataType === 'records' ) {
            canaryData[ typeKey ][ n ].recordLookUpDate = new Date();
          }
        }
      }
    }
  }



  /**
   * The request handler. Returns a promise that passes the requested data into its resolution.
   * @param {string} typeKey The typeKey for the list or record to look up.
   * @param {string} id The identifier for the record to lookup is optional.
   * @returns {object} Promise.
   */
  function doRequest(typeKey, id) {

    var promise = new Ember.RSVP.Promise(function(resolve){

      /** Handle the resolution of the promise, passing the requested data to resolution callbacks. */
      function complete() {
        var payload = get( typeKey, id );
        console.log( payload );
        resolve( payload );
      }

      var result = get( typeKey, id );

      /** If the requested data is already loaded, resolve. Otherwise, request it. Also, make sure records for monitors aren't too stale. */
      if ( typeof result === 'undefined' ) {
        result = load( typeKey );
        result.then( function (p) { p.then(function() {complete();}); } );
      } else if ( typeKey === 'MONITOR' && ifMonitorsNeedRefreshed( result ) ) {
        result = loadItemData( canaryData[ 'MONITOR' ], 'MONITOR' );
        result.then( function () { complete(); } );
      } else {
        complete();
      }

    });

    /** return the promise object */
    return promise;
  }



  /** The exposed interface. */
  Canary.store = {

    /** Get all members of a list of given type. */
    all: function(typeKey) {
      return doRequest(typeKey);
    },

    /** Get a specific record by type and identifier. */
    find: function(typeKey, id) {
      return doRequest(typeKey, id);
    }

  };

})();