$(document).ready(function(){
	$('#dp-start').datepicker();
	$('#dp-end').datepicker();
	$('#tp-start').timepicker();
	$('#tp-end').timepicker();
});

function init() {
	if (typeof (console) !== "undefined") {
		console.info("The fucking shit is being initialized!!!");
	}
	com.isd.bluecollar.init('https://' + window.location.host + '/_ah/api');
}