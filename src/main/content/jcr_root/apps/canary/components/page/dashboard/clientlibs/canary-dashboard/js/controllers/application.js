(function(){

  'use strict';

	Canary.ApplicationController = Ember.ArrayController.extend({

		// Properties:

		search: '',
		titleFilter: null,
		needs: ['alerts'],

		// Subsets:

		// Filter for search results.
		arrangedContent: function() {

			var search = this.get('search');
			if (!search) { return this.get('content') }

			return this.get('content').filter(function(note) {
				return note.get('title').indexOf(search) !== -1;
			});

		}.property('content', 'titleFilter'),

		// Actions:
		actions:{

			// When user enters a seach string, store it in the controller and redirect to search results route.
			query: function() {
				var query = this.get('search');
				this.transitionToRoute('search', encodeURIComponent(query));
			}
		}

	});

})();