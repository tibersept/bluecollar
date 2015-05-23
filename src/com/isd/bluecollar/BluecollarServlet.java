package com.isd.bluecollar;

import java.io.IOException;
import javax.servlet.http.*;

@SuppressWarnings("serial")
public class BluecollarServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("text/html");
		resp.getWriter().println("<html><body><!--empty--></body></html>");
	}
}
