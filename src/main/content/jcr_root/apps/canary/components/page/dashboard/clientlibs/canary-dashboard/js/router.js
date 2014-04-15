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
    },
    actions: {
      refresh: function () {
        this.refresh();
      },
      resetAlarm: function() {
        var id = this.controllerFor('monitor').get('identifier');
        var resetPromise = Canary.store.resetAlarm(id);
        var route = this;

        resetPromise.then(function(result) {
          route.refresh();
          route.controllerFor('monitor').set('alert', 'Monitor Alarm Reset Successfully');
        }, function(reason) {
          route.controllerFor('monitor').set('alert', 'Monitor Alarm Reset Failed. Error: "'+reason+'"');
        });
        return false;
      }
    }
  });

  Canary.MonitorResetRoute = Ember.Route.extend({
    model: function () {
      var id = this.controllerFor('monitor').get('identifier');
      //this.controllerFor('monitor').set('model', Canary.store.find('MONITOR', id));
      return Canary.store.resetAlarm(id);
    },
    renderTemplate: function() {
      this.render('monitorReset', {
        outlet: 'reset'
      });

    }
  });

  Canary.SearchRoute = Ember.Route.extend({
    model: function(params) {
      this.searchTerm = params.search_term;
      return Canary.store.contains('MONITOR', params.search_term);
    },
    setupController: function(controller, model) {
      controller.set('search', this.searchTerm);
      controller.set('content', model);
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