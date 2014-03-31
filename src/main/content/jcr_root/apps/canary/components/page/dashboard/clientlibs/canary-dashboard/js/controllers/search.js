(function(){

  'use strict';

  Canary.SearchController = Canary.MonitorsController.extend({

    needs: ['application'],

    itemController: 'monitor',

    // Subsets:

    // Search Results
    searchResults: Ember.arrayComputed('monitors', {

      addedItem: function (accumulatedValue, item) {
        if ( item.get( 'serviceName' ).indexOf( this.get( 'controllers.application.search' ) ) > -1 ) {
           accumulatedValue.pushObject( Canary.MonitorController.create( {model: item} ) );
        }
        return  accumulatedValue;
      },

      removedItem: function (accumulatedValue, item) {
        return  accumulatedValue.filter( function(i) { return i.get('id') !== item.get( 'id' ); } );
      }

    })

  });

})();