(function() {

  'use strict';

  Canary.Router.map(function() {

    this.resource( 'monitors', { path: '/' }, function() {
      this.resource( 'search', { path: '/search/:search_term' });
    });

    this.resource( 'search', { path: '/search/:search_term' });

    this.resource('monitor', { path: '/monitor/:monitor_id' });

    this.resource('settings');

    this.resource('help');

  });

  Canary.ApplicationRoute = Ember.Route.extend({
    actions: {
      // Go to a record detail page.
			viewRecord: function(id) {
        this.transitionTo('monitor', id);
      }
    }
  });

  Canary.MonitorsRoute = Ember.Route.extend({
    model: function() {
      return Canary.store.all('MONITOR');
    },
    renderTemplate: function() {
      this.render('monitors', {
        outlet: 'main'
      });
    }
  });

  Canary.MonitorRoute = Ember.Route.extend({
    model: function(params) {
      return Canary.store.find('MONITOR', params.monitor_id);
    },
    renderTemplate: function() {
      this.render('monitor', {
        outlet: 'main'
      });

    }
  });

  Canary.SearchRoute = Ember.Route.extend({
    setupController: function(controller) {
      controller.set('monitors', Canary.store.all('MONITOR'));
    },
    renderTemplate: function() {
      this.render('search', {
        outlet: 'main'
      });
    }
  });

  Canary.RecordsRoute = Ember.Route.extend({
    setupController: function(controller) {
      controller.set('content', this.controllerFor('monitor').get('records'));
      controller.get('graphData');
      controller.get('graphLabels');
    }
  });

})();