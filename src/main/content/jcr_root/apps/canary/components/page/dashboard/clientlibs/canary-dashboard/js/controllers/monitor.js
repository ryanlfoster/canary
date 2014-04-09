(function(){

  'use strict';

  Canary.MonitorController = Ember.ObjectController.extend({

    // Properties:

    isCardExpanded: false,

    // Calculated Values:

    isError: function() {
      return this.get('records.alarmed') === true;
    }.property('records.alarmed'),

    isNotError: function() {
      return this.get('records.alarmed') === false;
    }.property('records.alarmed'),

    isNormal: function() {
      return this.get('records.alarmed') === false;
    }.property('records.alarmed'),

    isNotNormal: function() {
      return this.get('records.alarmed') === true;
    }.property('records.alarmed'),

    hasRecords: function() {
      return this.get('records.records').length > 0;
    }.property('records.records'),

    // Latest Record

    lastLogged: function () {
      var newest = this.get('records.records')[0], newestDate = new Date(newest.startTime), compareDate;
      for (var n = 1; n < this.get('records.records').length; n++) {
        compareDate = new Date(this.get('records.records')[n].startTime);
        if (compareDate > newestDate) {
          newestDate = compareDate;
          newest = this.get('records.records')[n];
        }
      }
      return newest;
    }.property('records.records'),


    // Timestamp of last logged record.
    lastLoggedTime: function() {
      return this.get('lastLogged.startTime');
    }.property('lastLogged'),

    // Response type of last logged record.
    lastResponseType: function() {
      return this.getWithDefault('lastLogged.responseType', '--');
    }.property('lastLogged'),

    status: function () {
      return (this.get('records.alarmed')) ? 'ALARMED' : 'NORMAL';
    }.property('records.alarmed'),

    // Duration of last logged record.
    lastLoggedDuration: function() {
      return Canary.dateOffsetInMilliseconds(this.get('lastLogged.startTime'), this.get('lastLogged.endTime'));
    }.property('lastLogged'),

    // Cumulative Stats
    durations: function () {
      var array = [];
      for (var n = 0; n < this.get('records.records').length; n++) {
        array.push(Canary.dateOffsetInMilliseconds(this.get('records.records')[n].startTime, this.get('records.records')[n].endTime));
      }
      return array;
    }.property('records.records'),

    // Smallest observed duration.
    minDuration: Ember.computed.min('durations'),

    // Largest observed duration.
    maxDuration: Ember.computed.max('durations'),

    average: function () {
      var total = 0;
      for (var n = 0; n < this.get('records.records').length; n++) {
        total += Canary.dateOffsetInMilliseconds(this.get('records.records')[n].startTime, this.get('records.records')[n].endTime);
      }
      return Math.floor(total / this.get('records.records').length);
    }.property('records.records'),


    // Does this record have active allerts?
    hasActiveAlerts: Ember.reduceComputed('alerts', {
      initialValue: false,

      initialize: function (array, changeMeta, instanceMeta) {
        instanceMeta.activeAlerts = 0;
      },

      addedItem: function (accumulatedValue, item, changeMeta, instanceMeta) {
        if ( item.get( 'active' ) === true ) {
          instanceMeta.activeAlerts++;
          accumulatedValue = true;
        }
        return accumulatedValue;
      },

      removedItem: function (accumulatedValue, item, changeMeta, instanceMeta) {
        if ( item.get( 'active' ) === false ) {
          instanceMeta.activeAlerts--;
        }
        return instanceMeta.activeAlerts > 0;
      }
    }),

    // Actions:
    actions: {

      // Toggle the expanded/collapsed state of a card in the grid view.
      toggleCard: function() {
        this.set( 'isCardExpanded', !this.get('isCardExpanded') );
      },

      reset: function() {
        this.set( 'status', 'normal' );
        this.transitionToRoute('monitor', this.get('id'));
      }

    }

  });

})();