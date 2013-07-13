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
com.isd.bluecollar.SCOPES = 'https://www.googleapis.com/auth/userinfo.email';

/** Response type of the auth token */
com.isd.bluecollar.RESPONSE_TYPE = 'token id_token';

/**
 * Whether or not the user is signed in.
 * @type {boolean}
 */
com.isd.bluecollar.signedIn = false;

/**
 * Checks into a workday.
 */
com.isd.bluecollar.checkin = function() {
	gapi.client.bluecollar.wcard.checkin().execute(function(resp){		  
		if(console) {
			console.log(resp);
		}
	});
};

/**
 * Checks out of a workday.
 */
com.isd.bluecollar.checkout = function() {
	gapi.client.bluecollar.wcard.checkout().execute(function(resp){
		if(console) {
			console.log(resp);
		}
	});
};

/**
 * Lists the current workday.
 */
com.isd.bluecollar.list = function() {
	gapi.client.bluecollar.wcard.list({'date':'23-01-2013'}).execute(function(resp){
		if(console) {
			console.log(resp);
		}
	});
};

/**
 * Loads the application UI after the user has completed auth.
 */
com.isd.bluecollar.userAuthed = function() {
  var request = gapi.client.oauth2.userinfo.get().execute(function(resp) {
    if (!resp.code) {
      var token = gapi.auth.getToken();
      token.access_token = token.id_token;
      gapi.auth.setToken(token);
      com.isd.bluecollar.signedIn = true;
	  $('.login-content').hide();
      $('.main-content').show();
    }
  });
};

/**
 * Handles the authentication flow, with the given value for immediate mode.
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
 * Presents the user with the authorization popup.
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
};
