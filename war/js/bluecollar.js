/** com namespace */
var com = com || {};

/** com isd namespace */
com.isd = com.isd || {};

/** com isd bluecollar namespace */
com.isd.bluecollar = com.isd.bluecollar || {};

/** Client id of the application */
com.isd.bluecollar.CLIENT_ID ='458645171438.apps.googleusercontent.com';

/** Scopes used by the application */
com.isd.bluecollar.SCOPES = ['https://www.googleapis.com/auth/userinfo.email','https://www.googleapis.com/auth/drive'];

/** Response type of the auth token */
com.isd.bluecollar.RESPONSE_TYPE = 'token id_token';

/**
 * Whether or not the user is signed in.
 * @type {boolean}
 */
com.isd.bluecollar.signedIn = false;

/**
 * Indicates whether the Drive API is loaded.
 * @type {boolean}
 */
com.isd.bluecollar.driveOk = false;

/**
 * Indicates whether the debug mode is on.
 * @type {boolean}
 */
com.isd.bluecollar.debugMode = false;

/**
 * Original access token provided on authorization.
 * @type {String}
 */
com.isd.bluecollar.originalAccessToken = null;

/**
 * REST call. Checks into a workday.
 */
com.isd.bluecollar.checkin = function() {
	var combo = $('.combo-project-selection');
	var currentProject = combo.find(':selected').val();
	gapi.client.bluecollar.wcard.checkin({'project': currentProject}).execute(function(resp){		  
		if( resp ) {
			var dateString = resp.string;			
			com.isd.bluecollar.startTimer( dateString );
			combo.prop('disabled',true);
			$('.btn-start').hide();
			$('.btn-stop').show();
		}
	});
	return false;
};

/**
 * REST call. Checks out of a workday.
 */
com.isd.bluecollar.checkout = function() {
	var combo = $('.combo-project-selection');
	var currentProject = combo.find(':selected').val();
	gapi.client.bluecollar.wcard.checkout({'project': currentProject}).execute(function(resp){
		if(resp) {
			var dateString = resp.string;
			com.isd.bluecollar.stopTimer( dateString ); 
			combo.prop('disabled',false);
			$('.btn-stop').hide();
			$('.btn-start').show();
		}
	});
	return false;
};

/**
 * REST call. Checks the active project of a workday.
 */
com.isd.bluecollar.checkactive = function() {
	var combo = $('.combo-project-selection');
	gapi.client.bluecollar.wcard.checkactive().execute(function(resp){
		if(resp && resp.status === 'ok') {
			if( resp.project && resp.projectBegin ) {
				var dateString = resp.projectBegin;
				com.isd.bluecollar.startTimer( dateString );
				combo.val(resp.project);
				combo.prop('disabled',false);
				$('.btn-stop').show();
				$('.btn-start').hide();
			}
		}
	});
	return false;
};

/**
 * REST call. Lists the current workday.
 * @param start the report range start
 * @param end the report range end
 * @param timezone the user time zone as string
 */
com.isd.bluecollar.generateReport = function( start, end, timezone ) {
	com.isd.bluecollar.busy.show();
	gapi.client.bluecollar.wcard.generatereport({'begin':start,'end':end,'timezone':timezone}).execute(function(resp){
		if( resp.byteArray ) {
			com.isd.bluecollar.filemanager.storeFile(resp.name, resp.byteArray, !com.isd.bluecollar.debugMode);
		} else {
			com.isd.bluecollar.displayMessage('Error', 'Report generation has faltered!');
		}
		com.isd.bluecollar.busy.hide();
	});
};

/**
 * REST call. Adds a project to the list of user projects.
 */
com.isd.bluecollar.addProject = function( name, description ) {
	gapi.client.bluecollar.wcard.addproject({'name':name,'description':description}).execute(function(resp){
		if( resp ) {
			com.isd.bluecollar.updateProjectList(resp.itemList);
			com.isd.bluecollar.clearProjectForm();
		}
	});
};

/**
 * REST call. Lists the projects of the user. 
 */
com.isd.bluecollar.listProjects = function() {
	gapi.client.bluecollar.wcard.listprojects().execute(function(resp){
		if( resp ) {			
			com.isd.bluecollar.updateProjectList(resp.itemList);
		}
	});
};

/**
 * REST call. Sets a user setting.
 * @param setting the user setting
 * @param value the user setting value 
 */
com.isd.bluecollar.setUserSetting = function( setting, value ) {
	gapi.client.bluecollar.wcard.setusersetting({'setting':setting,'value':value}).execute(function(resp){
		if( resp ) {
			if( resp.status !== 'ok' ) {
				com.isd.bluecollar.displayMessage("Error", "No projects were found! You can add projects in settings.");
			}
		}
	});
};

/**
 * REST call. Loads a user setting.
 * @param setting the setting name
 */
com.isd.bluecollar.loadUserSetting = function( setting ) {
	gapi.client.bluecollar.wcard.getusersetting({'setting':setting}).execute(function(resp) {
		if( resp ) {
			if( resp.string ) {
				com.isd.bluecollar.updateSingleSetting(setting, resp.string);
			}
		}
	});
};

/**
 * REST call. Loads all user settings.
 */
com.isd.bluecollar.loadAllSettings = function() {
	gapi.client.bluecollar.wcard.getallsettings().execute(function(resp){
		if( resp ) {
			com.isd.bluecollar.updateReportSettings(resp);
		}
	});
}

/**
 * Authentication. Loads the application UI after the user has completed auth.
 */
com.isd.bluecollar.userAuthed = function() {
  var request = gapi.client.oauth2.userinfo.get().execute(function(resp) {
	  if (!resp.code) {
		  var token = gapi.auth.getToken();
		  com.isd.bluecollar.originalAccessToken = token.access_token;
		  token.access_token = token.id_token;
		  gapi.auth.setToken(token);
		  com.isd.bluecollar.loadAdditionalApis();
		  com.isd.bluecollar.signedIn = true;	
		  com.isd.bluecollar.switchToMain();
	  }
  });
};

/**
 * Authentication. Handles the authentication flow, with the given value for immediate mode.
 * @param {boolean} mode Whether or not to use immediate mode.
 * @param {Function} callback Callback to call on completion.
 */
com.isd.bluecollar.signin = function(mode, callback) {
	gapi.auth.authorize({client_id: com.isd.bluecollar.CLIENT_ID,
		scope: com.isd.bluecollar.SCOPES,
		immediate: mode,
		response_type: com.isd.bluecollar.RESPONSE_TYPE},
		callback);
};

/**
 * Authentication. Signs the currently logged user out.
 */
com.isd.bluecollar.signout = function() {
	if (com.isd.bluecollar.signedIn) {
		com.isd.bluecollar.auth();
	}
	com.isd.bluecollar.switchToLogin();
	com.isd.bluecollar.stopTimer(null);
	return false;
};

/**
 * Authentication. Presents the user with the authorization popup.
 */
com.isd.bluecollar.auth = function() {
  if (!com.isd.bluecollar.signedIn) {
	  com.isd.bluecollar.signin(false,com.isd.bluecollar.userAuthed);
  } else {
	  com.isd.bluecollar.signedIn = false;
	  gapi.auth.setToken(null);
  }
  return false;
};

/**
 * Switches to main perspective.
 * @return returns <code>false</code> in case method is invoked directly from a button
 */
com.isd.bluecollar.switchToMain = function() {
		com.isd.bluecollar.checkactive();
		com.isd.bluecollar.listProjects();
		com.isd.bluecollar.loadAllSettings();
		$('.login-content').hide();
		$('.main-content').show();
		return false;
};

/**
 * Switches to login perspective.
 * @return returns <code>false</code> in case method is invoked directly from a button
 */
com.isd.bluecollar.switchToLogin = function() {
	$('.main-content').hide();
	$('.login-content').show();
	return false;
};

/**
 * Updates the report settings.
 * @param settings the settings
 */
com.isd.bluecollar.updateReportSettings = function( settings ) {
	if( settings.keyList && settings.valueList ) {
		var keys = settings.keyList;
		var vals = settings.valueList;
		if( keys.length === vals.length ) {
			for( var i=0; i<keys.length; i++ ) {
				com.isd.bluecollar.updateSingleSetting( keys[i], vals[i] );
			}
		}
	}
};

/**
 * Updates a single report setting.
 * @param setting the user setting
 * @param value the value to which the setting is set
 */
com.isd.bluecollar.updateSingleSetting = function( setting, value ) {
	switch( setting ) {
	case 'reportUser':
		$('#report-user-name').val(value);
		break;
	case 'companyName':
		$('#company-name').val(value);
		break;
	case 'language':
		$('#report-language').val(value);
		break;
	default:
		// do nothing
	}
};

/**
 * Submits a new project.
 */
com.isd.bluecollar.submitNewProject = function() {
	var name = $('.input-project-name').val();
	var description = $('.input-project-description').val();
	com.isd.bluecollar.addProject(name,description);
	return false;
};

/**
 * Updates the project list with the projects provided in the array.
 * @param projects the project list
 */
com.isd.bluecollar.updateProjectList = function( projects ) {
	if( projects ) {
		if( projects.length==0) {
			com.isd.bluecollar.displayMessage("Info", "No projects were found! You can add projects in settings.");
			com.isd.bluecollar.updateProjectTable([]);
		} else {
			com.isd.bluecollar.updateProjectSelection( projects );
			com.isd.bluecollar.updateProjectTable( projects );
		}
	}
};

/**
 * Updates the project selection combo box in the main screen.
 * @param projects the project list
 */
com.isd.bluecollar.updateProjectSelection = function( projects ) {
	if( projects ) {
		var content = '';
		for( var i=0; i<projects.length; i++ ) {
			var project = projects[i];
			content += '<option value="'+project+'">'+project+'</option>';
		}
		$('.combo-project-selection').html(content);
	}
};

/**
 * Updates the project table on the settings page.
 * @param projects the project list
 */
com.isd.bluecollar.updateProjectTable = function( projects ) {
	if( projects ) {
		var content = '';
		if( projects.length == 0 ) {
			content += '<tr><td>No projects found</td></tr>';
		} else {
			for( var i=0; i<projects.length; i++ ) {
				var project = projects[i];
				content += '<tr><td>'+project+'</td></tr>';
			}
		}
		$('.table-projects').html(content);
	}
};

/**
 * Clears the project form.
 */
com.isd.bluecollar.clearProjectForm = function() {
	 $('.input-project-name').val('');
	 $('.input-project-description').val('');
};

/**
 * Handles tab activation.
 * @param e the tab activation event
 */
com.isd.bluecollar.onTabActivation = function( e ) {	
	var tabAnchor = $(e.target);	
	if ( tabAnchor ) {
		var href = tabAnchor.attr('href');
		switch(href) {
		case "#settings-project":
			com.isd.bluecollar.listProjects();
			break;
		default:
			break;
		}
	}
};

/**
 * Generates a report on activation of the generate report button.
 */
com.isd.bluecollar.provideReport = function() {
	if( com.isd.bluecollar.driveOk || com.isd.bluecollar.debugMode ) {
		var dateStart = $('#dp-start').val();
		var timeStart = $('#tp-start').val();
		var start = com.isd.bluecollar.date.parseDate(dateStart, timeStart);
		var dateEnd = $('#dp-end').val();
		var timeEnd = $('#tp-end').val();
		var end = com.isd.bluecollar.date.parseDate(dateEnd, timeEnd);
		
		if( com.isd.bluecollar.date.checkRangeValidity(start, end) ) {
			var timezone = jstz.determine();
			com.isd.bluecollar.generateReport(start,end, timezone.name());	
		} else {
			com.isd.bluecollar.displayMessage("Error", "The selected range for report generation is invalid!");
		}		
	} else {
		com.isd.bluecollar.displayMessage("Error", "Report generation is not supported without authorized access to Google Drive!");
	}
	return false;
};

/**
 * Updates the user settings.
 */
com.isd.bluecollar.updateSettings = function() {
	var user = $('#report-user-name').val();
	if( user!=null && user.length>0 ) {
		com.isd.bluecollar.setUserSetting('reportUser', user);
	}
	var company = $('#company-name').val();
	if( company!=null && company.length>0 ) {
		com.isd.bluecollar.setUserSetting('companyName', company);
	}
	var language = $('#report-language option:selected').val();
	if( language!=null && language.length>0 ) {
		com.isd.bluecollar.setUserSetting('language', language);
	}
	return false;
};

/**
 * Starts the project timer.
 * @param utcTimestamp the UTC timestamp for start time
 */
com.isd.bluecollar.startTimer = function( utcTimestamp ) {
	com.isd.bluecollar.updateTimer(utcTimestamp);
	com.isd.bluecollar.timer = window.setInterval(function(){com.isd.bluecollar.updateTimer(utcTimestamp);}, 4000);
};

/**
 * Updates the project timer.
 * @param start the start date
 */
com.isd.bluecollar.updateTimer = function( utcStartTimestamp ) {
	var elm = $('#time-display');
	if( utcStartTimestamp && elm ) {
		var value = com.isd.bluecollar.date.getFormattedClockDiff(utcStartTimestamp);
		elm.html(value);
	}
};

/**
 * Stops the project timer.
 * @param utcTimestamp the UTC timestamp for end time
 */
com.isd.bluecollar.stopTimer = function( utcTimestamp ) {
	window.clearInterval(com.isd.bluecollar.timer);
	var elm = $('#time-display');
	if( elm ) {
		elm.html("00:00:00");
	}
};

/**
 * Displays a message to the user in the alert box.
 * @param type the main type of the message, e.g. info, warning, debug, etc.
 * @param message the message
 */
com.isd.bluecollar.displayMessage = function( type, message ) {
	var box = $('.alert-box');
	if( box ) {
		box.children().remove();
		var content = '<div class="alert alert-info"><button type="button" class="close" data-dismiss="alert">&times;</button><strong>'+
			type + '!</strong> ' + message + '</div>';
		box.append(content);
	}
};

/**
 * Enables the login buttons on login screen.
 */
com.isd.bluecollar.enableLogin = function() {
	$('.btn-signin').prop('disabled', false);
	$('.btn-tryit').prop('disabled', false);
};

/**
 * Initializes the report range.
 */
com.isd.bluecollar.initDatepickers = function() {
	var now = new Date();
	var first = new Date(now.getFullYear(), now.getMonth(), 1);
	$('#dp-start').val(com.isd.bluecollar.date.toDateString(first));
	var last = new Date(now.getFullYear(), now.getMonth()+1, 0);
	$('#dp-end').val(com.isd.bluecollar.date.toDateString(last));
	
	// initialize the datepickers
	$('#dp-start').datepicker();
	$('#dp-end').datepicker();
	$('#tp-start').timepicker();
	$('#tp-end').timepicker();
};

/**
 * Loads additional APIs.
 */
com.isd.bluecollar.loadAdditionalApis = function() {
	var driveCallback = function() {
		com.isd.bluecollar.driveOk = true;
	};
	gapi.client.load('drive', 'v2', driveCallback);
};

/**
 * Initializes the application.
 * @param {string} apiRoot Root of the API's path.
 */
com.isd.bluecollar.init = function(apiRoot) {
	var apisToLoad;
	var callback = function() {
		if (--apisToLoad == 0) {
			com.isd.bluecollar.busy.hide();
			com.isd.bluecollar.signin(true,com.isd.bluecollar.userAuthed);
			if( !com.isd.bluecollar.signedIn ) {
				com.isd.bluecollar.enableLogin();
			}
		}
	}
	
	apisToLoad = 2;
	gapi.client.load('bluecollar', 'v2', callback, apiRoot);
	gapi.client.load('oauth2', 'v2', callback);

	$('.btn-start').click(com.isd.bluecollar.checkin);
	$('.btn-stop').click(com.isd.bluecollar.checkout);
	$('.btn-signin').click(com.isd.bluecollar.auth);
	$('.btn-sign-out').click(com.isd.bluecollar.signout);
	$('.btn-tryit').click(com.isd.bluecollar.switchToMain);
	$('.btn-add-project').click(com.isd.bluecollar.submitNewProject);
	$('.btn-generate-report').click(com.isd.bluecollar.provideReport);
	$('.btn-update-settings').click(com.isd.bluecollar.updateSettings)
	
	/* Tab activation handling */
	$('a[data-toggle="tab"]').on('show', com.isd.bluecollar.onTabActivation);
};
