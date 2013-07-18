if( com.isd.bluecollar ) {
	com.isd.bluecollar.date = com.isd.bluecollar.date || {};
	
	/**
	 * Parses a date string and returns the same date with the format expected
	 * by the server.
	 * @param dateString a date string 
	 * @param timeString a time string
	 * @return the UTC timestamp matching the date and the time
	 */
	com.isd.bluecollar.date.parseDate = function( dateString, timeString ) {
		var date = new Date();
		com.isd.bluecollar.date.setDate(date,dateString);
		com.isd.bluecollar.date.setTime(date,timeString);
		return date.getTime();
	};
	
	/**
	 * Sets the date contained in the date string as a date for the date object.
	 * @param date the date object
	 * @param dateString the date string
	 */
	com.isd.bluecollar.date.setDate = function( date, dateString ) {
		var els = dateString.split("/");
		date.setMonth(els[0]);
		date.setDate(els[1]);
		date.setYear(els[2]);
	};
	
	/**
	 * Sets the time contained in the time string as a date for the date object.
	 * @param date the date object
	 * @param timeString the time string
	 */
	com.isd.bluecollar.date.setTime = function( date, timeString ) {
		var pm = timeString.indexOf("PM")>=0;
		timeString = timeString.substring(0,5);
		els = timeString.split(":");
		if( pm ) {
			var hrs = els[0];
			if( hrs != "12" ) {
				hrs = hrs+12;
			}
			date.setHours(hrs);
		} else {
			var hrs = els[0];
			if( hrs == "12" ) {
				hrs = 0;
			}
			date.setHours(hrs);
		}
		date.setMinutes(els[1]);
		date.setSeconds(0);
	};
	
	/**
	 * Converts the UTC epoch long value to a local javascript date.
	 * @param utcMilliseconds UTC milliseconds from the epoch
	 */
	com.isd.bluecollar.date.utcTimestampToLocalDate = function( utcMilliseconds ) {
		var dte = new Date(0);
		dte.setUTCMilliseconds(utcMilliseconds);
		return dte;
	};
	
	/**
	 * Calculates the difference between the UTC timestamp and now. Returns the difference
	 * as a chronometer value.
	 * @param utcTimestamp the UTC timestamp
	 * @return the clock difference as a string
	 */
	com.isd.bluecollar.date.getFormattedClockDiff = function( utcTimestamp ) {
		var diff = com.isd.bluecollar.date.getClockDiff(utcStartTimestamp);
		var hrs = com.isd.bluecollar.date.formatClockValue(diff[0],null);
		var min = com.isd.bluecollar.date.formatClockValue(diff[1],59);
		var sec = com.isd.bluecollar.date.formatClockValue(diff[2],59);
		return hrs + ":" + min + ":" + sec;
	};
	
	/**
	 * Calculates the difference between the UTC timestamp and now. Returns an array
	 * indicating the clock difference, i.e. hours, minutes, seconds.
	 * @param utcTimestamp the UTC timestamp of a past date
	 * @return the clock difference array, hours, minutes seconds 
	 */
	com.isd.bluecollar.date.getClockDiff = function( utcTimestamp ) {
		var now = new Date();
		var dif = Math.max(now.getTime() - utcTimestamp, 0);
		var hrs = Math.floor(dif/3600000);
		dif = dif - (hrs*3600000);
		var min = Math.floor(dif/60000);
		dif = dif - (min*60000);
		var sec = Math.round(dif/1000);
		return [hrs,min,sec];		
	};
	
	/**
	 * Formats a clock value by padding the value if necessary or restricting its maximum value.
	 * @param value the value
	 * @param max the maximum allowed value
	 * @return the formatted clock value
	 */
	com.isd.bluecollar.date.formatClockValue = function( value, max ) {
		if( value<10 ) {
			return "0"+value;
		} else if( max!=null && value > max ) {
			return max;
		}
	};
}