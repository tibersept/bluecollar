if( com.isd.bluecollar ) {
	com.isd.bluecollar.busy = com.isd.bluecollar.busy || {};
	
	/**
	 * Field indicates whether busy indicator is currently running
	 * @type {boolean}
	 */
	com.isd.bluecollar.busy._running = false;
	com.isd.bluecollar.busy._spinner = null;
	
	/**
	 * Displays the busy screen.
	 */
	com.isd.bluecollar.busy.show = function() {
		if( !com.isd.bluecollar.busy._running ) {
			var indicator = $('#busy-indicator');
			var opts = {
				  lines: 13, // The number of lines to draw
				  length: 6, // The length of each line
				  width: 30, // The line thickness
				  radius: 60, // The radius of the inner circle
				  corners: 1, // Corner roundness (0..1)
				  rotate: 11, // The rotation offset
				  direction: 1, // 1: clockwise, -1: counterclockwise
				  color: '#000', // #rgb or #rrggbb
				  speed: 1, // Rounds per second
				  trail: 13, // Afterglow percentage
				  shadow: false, // Whether to render a shadow
				  hwaccel: true, // Whether to use hardware acceleration
				  className: 'spinner', // The CSS class to assign to the spinner
				  zIndex: 2e9, // The z-index (defaults to 2000000000)
				  top: 'auto', // Top position relative to parent in px
				  left: 'auto' // Left position relative to parent in px
			};
			if( indicator.size() > 0 ) {
				var spinner = new Spinner(opts).spin(indicator.get(0));
				indicator.show();
				com.isd.bluecollar.busy._spinner = spinner;
				com.isd.bluecollar.busy._running = true;
			}
		}
	};
	
	/**
	 * Hides the busy screen.
	 */
	com.isd.bluecollar.busy.hide = function() {
		if( com.isd.bluecollar.busy._running ) {
			if( com.isd.bluecollar.busy._spinner ) {
				com.isd.bluecollar.busy._spinner.stop();
			}
			$('#busy-indicator').hide();
			com.isd.bluecollar.busy._running = false;			
		}
	};
}