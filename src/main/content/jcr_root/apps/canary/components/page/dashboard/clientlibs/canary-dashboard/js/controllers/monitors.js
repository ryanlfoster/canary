(function(){

  'use strict';

  Canary.MonitorsController = Ember.ArrayController.extend({

    itemController: 'monitor',

		// Properties:

    showGridView: false,

		// Counts:

    total: function() {
      return this.get('length');
    }.property('@each'),

    errorCount: function() {
      return this.filterBy('records.alarmed', true).get('length');
    }.property('@each.isError'),

    warnCount: function() {
      return this.filterBy('isWarning', true).get('length');
    }.property('@each.isWarning'),

    normalCount: function() {
      return this.filterBy('isNormal', true).get('length');
    }.property('@each.isNormal'),

    haveAlerts: function () {
      return this.filterBy('hasAlert', true).get('length') > 0;
    }.property('@each'),

    // Subsets:

    errors: function() {
      return this.filterBy('isError', true);
    }.property('@each.isError'),

    warnings: function() {
      return this.filterBy('isWarning', true);
    }.property('@each.isWarning'),

    normals: function() {
      return this.filterBy('isNormal', true);
    }.property('@each.isNormal'),

		// Actions:
		actions: {

			// Show the grid view.
			showGridView: function() {
				this.set('showGridView', true);
			},

			// Show the table view.
			showTableview: function() {
				this.set('showGridView', false);
			}

		}

  });

})();