(function() {

  'use strict';

  Canary.MonitorsView = Ember.View.extend({

    didInsertElement: function() {
			Ember.run.scheduleOnce('afterRender', this, 'tooltips');
			Ember.run.debounce(this, this.tooltips, 100);
    },

    tooltips: function () {
	    $('.has-tooltip').tooltip();
    }

  });

})();