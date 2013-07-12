$(document).ready(function(){
	$('#dp-start').datepicker();
	$('#dp-end').datepicker();
	$('#tp-start').timepicker();
	$('#tp-end').timepicker();
});

function init() {
	com.isd.bluecollar.init('https://' + window.location.host + '/_ah/api');
}