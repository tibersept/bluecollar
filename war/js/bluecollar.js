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
			var dateString = resp.date;			
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
			var dateString = resp.date;
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
 */
com.isd.bluecollar.generateReport = function() {
	gapi.client.bluecollar.wcard.generatereport({'begin':'2013-01-23 00:00:00','end':'2013-01-26 00:00:00'}).execute(function(resp){
		if( resp.byteArray ) {
			var token = gapi.auth.getToken();
			token.access_token = com.isd.bluecollar.originalAccessToken;			
			gapi.auth.setToken(token);
			
			com.isd.bluecollar.storeFile( resp.byteArray );
			
			var token = gapi.auth.getToken();
			token.access_token = token.id_token;
			gapi.auth.setToken(token);
		}
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
	com.isd.bluecollar.updateProjectSelection( projects );
	com.isd.bluecollar.updateProjectTable( projects );
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
 * Generates a report.
 */
com.isd.bluecollar.provideReport = function() {
	if( com.isd.bluecollar.driveOk ) {
		com.isd.bluecollar.generateReport();
	} else {
		if (typeof (console) !== "undefined") {
			console.info("Report generation is not supported without authorized access to Google Drive!");
		}
	}
	return false;
};

/**
 * Starts the project timer.
 * @param dateString the date string
 */
com.isd.bluecollar.startTimer = function( dateString ) {
	var utcMilliseconds = Date.parse(dateString);
	var dte = new Date(0);
	dte.setUTCMilliseconds(utcMilliseconds);
	com.isd.bluecollar.timer = window.setInterval(function(){com.isd.bluecollar.updateTimer(dte);}, 5000);
};

/**
 * Updates the project timer.
 * @param start the start date
 */
com.isd.bluecollar.updateTimer = function( start ) {
	var elm = $('#time-display');
	if( start && elm ) {
		var now = new Date();
		var dif = now.getTime() - start.getTime();
		var hrs = Math.floor(dif/3600000);
		dif = dif - (hrs*3600000);
		var min = Math.floor(dif/60000);
		dif = dif - (min*60000);
		var sec = Math.round(dif/1000);
		if( hrs<10 ) {
			hrs = "0"+hrs;
		}
		if( min<10 ) {
			min = "0"+min;
		} else if( min > 59 ) {
			min = "59";
		}
		if( sec<10 ) {
			sec = "0"+sec;
		} else if( sec > 59 ) {
			sec = "59";
		}
		var content = hrs + ":" + min + ":" + sec;
		elm.html(content);
	}
};

/**
 * Stops the project timer.
 */
com.isd.bluecollar.stopTimer = function( dateString ) {
	window.clearInterval(com.isd.bluecollar.timer);
	var elm = $('#time-display');
	if( elm ) {
		elm.html("00:00:00");
	}
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
