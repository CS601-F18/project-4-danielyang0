package cs601.project4.web;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

public class FormatedResponse {
	public static void get400Response(HttpServletResponse response, String title, String msg) throws IOException {
		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		response.setContentType("text/html");

		PrintWriter out = response.getWriter();

		out.println("<html><title>"+title+"</title><body>" + msg + "!</body></html>");

	}
}
