/**
 * Initializes datepickers.
 */
$(document).ready(function(){
	$('#dp-start').datepicker();
	$('#dp-end').datepicker();
	$('#tp-start').timepicker();
	$('#tp-end').timepicker();
});

/**
 * Initializes the google authorization and endpoint apis.
 */
function init() {	
	var protocol = window.location.protocol;
	com.isd.bluecollar.init(protocol + '//' + window.location.host + '/_ah/api');
}

/**
 * Checks whether the provided HREF contains a localhost reference.
 * @param href the HREF
 * @return <code>true</code> if the HREF contains a localhost reference
 */
function isLocalhost( href ) {
	if( href ) {
		return (href.indexOf("localhost")>-1 || href.indexOf("127.0.0.1")>-1);
	}
	return false;
}

/**
 * Ensures that the site is accessed over an HTTPS scheme if the URL does
 * not contain a localhost reference.
 */
function ensureHttpsScheme() {
	var href = window.location.href;
	if( window.location.protocol == "http:" &&  !isLocalhost(href)) {
		var rest = href.substr(5);
		window.location.replace("https:" + rest);
	}
}

ensureHttpsScheme();