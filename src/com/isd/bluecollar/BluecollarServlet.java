/**
 * 23.05.2015
 */
package com.isd.bluecollar;

import java.io.IOException;
import javax.servlet.http.*;

/**
 * Basic entry point to the application. Provides no data.
 * @author doan
 */
@SuppressWarnings("serial")
public class BluecollarServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("text/html");
		resp.getWriter().println("<html><body><!--empty--></body></html>");
	}
}
