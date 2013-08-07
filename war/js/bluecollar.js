/*
 * Doan Isakov, 2013, All rights reservered. 
 */
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
 * REST call. Lists the current workday.
 * @param start the report range start
 * @param end the report range end
 * @param timezone the user time zone as string
 */
com.isd.bluecollar.generateReport = function( start, end, timezone ) {	
	gapi.client.bluecollar.wcard.generatereport({'begin':start,'end':end,'timezone':timezone}).execute(function(resp){
//		if( resp.byteArray ) {
//			var token = gapi.auth.getToken();
//			token.access_token = com.isd.bluecollar.originalAccessToken;			
//			gapi.auth.setToken(token);
//			
//			com.isd.bluecollar.filemanager.storeFile( resp.byteArray );
//			
//			var token = gapi.auth.getToken();
//			token.access_token = token.id_token;
//			gapi.auth.setToken(token);
//		}
		com.isd.bluecollar.displayMessage("Info", "Report has been genrated");
	});
};

/**
 * REST call. Adds a project to the list of user projects.
 */
com.isd.bluecollar.addProject = function( name, description ) {
	gapi.client.bluecollar.wcard.addproject({'name':name,'description':description}).execute(function(resp){
		if( resp ) {
			com.isd.bluecollar.updateProjectList(resp.items);
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
			com.isd.bluecollar.updateProjectList(resp.items);
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
 * Authentication. Presents the user with the authorization popup.
 */
com.isd.bluecollar.auth = function() {
  if (!com.isd.bluecollar.signedIn) {
	  com.isd.bluecollar.signin(false,com.isd.bluecollar.userAuthed);
  } else {
	  com.isd.bluecollar.signedIn = false;
  }
  return false;
};

/**
 * Switches to main perspective.
 * @return returns <code>false</code> in case method is invoked directly from a button
 */
com.isd.bluecollar.switchToMain = function() {
	  $('.login-content').hide();
      $('.main-content').show();
      com.isd.bluecollar.listProjects();
      return false;
};

/**
 * Switches to login perspective.
 * @return returns <code>false</code> in case method is invoked directly from a button
 */
com.isd.bluecollar.swtichToLogin = function() {
	$('.main-content').hide();
	$('.login-content').show();
	return false;
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
	if( projects.length==0) {
		com.isd.bluecollar.displayMessage("Info", "No projects were found! You can add projects in settings.");
	} else {
		com.isd.bluecollar.updateProjectSelection( projects );
		com.isd.bluecollar.updateProjectTable( projects );	
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
		for( var i=0; i<projects.length; i++ ) {
			var project = projects[i];
			content += '<tr><td>'+project+'</td></tr>';
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
		case "#settings":
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
//	if( com.isd.bluecollar.driveOk ) {
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
//	} else {
//		com.isd.bluecollar.displayMessage("Error", "Report generation is not supported without authorized access to Google Drive!");
//	}
	return false;
};

/**
 * Starts the project timer.
 * @param utcTimestamp the UTC timestamp for start time
 */
com.isd.bluecollar.startTimer = function( utcTimestamp ) {		
	com.isd.bluecollar.timer = window.setInterval(function(){com.isd.bluecollar.updateTimer(utcTimestamp);}, 5000);
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
			com.isd.bluecollar.signin(true,com.isd.bluecollar.userAuthed);
			if( !com.isd.bluecollar.signedIn ) {
				com.isd.bluecollar.enableLogin();
			}
		}
	}
	
	apisToLoad = 2;
	gapi.client.load('bluecollar', 'v1', callback, apiRoot);
	gapi.client.load('oauth2', 'v2', callback);

	$('.btn-start').click(com.isd.bluecollar.checkin);
	$('.btn-stop').click(com.isd.bluecollar.checkout);
	$('.btn-signin').click(com.isd.bluecollar.auth);
	$('.btn-tryit').click(com.isd.bluecollar.switchToMain);
	$('.btn-add-project').click(com.isd.bluecollar.submitNewProject);
	$('.btn-generate-report').click(com.isd.bluecollar.provideReport);
	
	/* Tab activation handling */
	$('a[data-toggle="tab"]').on('show', com.isd.bluecollar.onTabActivation);
};
