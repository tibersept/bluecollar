if( com.isd.bluecollar ) {
	com.isd.bluecollar.filemanager = com.isd.bluecollar.filemanager || {};
	
	/**
	 * Stores a file's BASE64 content on the user's Google Drive.
	 * @param reportName the name of the report
	 * @param report the BASE64 encoded content of the report
	 */
	com.isd.bluecollar.filemanager.storeFile = function( reportName, report ) {
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
			console.log(file);			
		};
		request.execute(callback);
	};
}