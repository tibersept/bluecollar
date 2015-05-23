if( com.isd.bluecollar ) {
	com.isd.bluecollar.filemanager = com.isd.bluecollar.filemanager || {};
	
	/**
	 * Stores a file's BASE64 content on the user's Google Drive or in an anchor ready for download.
	 * @param reportName the name of the report
	 * @param report the BASE64 encoded content of the report
	 * @param gdrive use gdrive to store the file
	 */
	com.isd.bluecollar.filemanager.storeFile = function( reportName, report, gdrive ) {
		if( gdrive ) {
			com.isd.bluecollar.filemanager._getGDriveToken();
			com.isd.bluecollar.filemanager._storeOnGDrive(reportName, report);
			com.isd.bluecollar.filemanager._forgetGDriveToken();
		} else {
			com.isd.bluecollar.filemanager._generateAnchor(reportName, report);
		}
	};
	
	/**
	 * Acquires a new Google Drive access token.
	 */
	com.isd.bluecollar.filemanager._getGDriveToken = function() {
		var token = gapi.auth.getToken();
		token.access_token = com.isd.bluecollar.originalAccessToken;			
		gapi.auth.setToken(token);
	};
	
	/**
	 * Revokes the Google Drive access token and restores the original one.
	 */
	com.isd.bluecollar.filemanager._forgetGDriveToken = function() {
		var token = gapi.auth.getToken();
		token.access_token = token.id_token;
		gapi.auth.setToken(token);
	};
	
	/**
	 * Stores a file's BASE64 content on the user's Google Drive.
	 * @param reportName the name of the report
	 * @param report the BASE64 encoded content of the report
	 */
	com.isd.bluecollar.filemanager._storeOnGDrive = function( reportName, report ) {
		const boundary = '-------314159265358979323846';
		const delimiter = "\r\n--" + boundary + "\r\n";
		const close_delim = "\r\n--" + boundary + "--";

		var contentType = 'application/octet-stream';
		var metadata = {
				'title': reportName,
				'mimeType': contentType
		};

		var base64Data = report;
		var multipartRequestBody =
			delimiter +
			'Content-Type: application/json\r\n\r\n' +
			JSON.stringify(metadata) +
			delimiter +
			'Content-Type: ' + contentType + '\r\n' +
			'Content-Transfer-Encoding: base64\r\n' +
			'\r\n' +
			base64Data +
			close_delim;

		var request = gapi.client.request({
			'path': '/upload/drive/v2/files',
			'method': 'POST',
			'params': {'uploadType': 'multipart'},
			'headers': {
				'Content-Type': 'multipart/mixed; boundary="' + boundary + '"'
			},
			'body': multipartRequestBody});		
		var callback = function(file) {
			com.isd.bluecollar.displayMessage('Info', 'Report has been generated! <a href="'+file.webContentLink+'">Download it here!</a>');
		};
		request.execute(callback);
	};
	
	/**
	 * Stores a file's BASE64 content in an anchor ready for download.
	 * @param reportName the name of the report
	 * @param report the BASE64 encoded content of the report
	 */
	com.isd.bluecollar.filemanager._generateAnchor = function(reportName, report) {
		var link = 'data:application/octet-stream;charset=utf-8;base64,';
		link += report;
		com.isd.bluecollar.displayMessage('Info', 'Report has been generated! <a download="'+reportName+'" href="'+link+'">Download it here!</a>');
	};
}