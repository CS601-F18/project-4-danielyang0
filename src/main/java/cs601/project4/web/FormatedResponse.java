package cs601.project4.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import cs601.project4.bean.Event;
import cs601.project4.dbservice.EventDBService;
import cs601.project4.dbservice.EventDBServiceImpl;
import cs601.project4.dbservice.DBServiceProxy;

public class FormatedResponse {
	public static void get400Response(HttpServletResponse response, String title, String msg) throws IOException {
		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		response.setContentType("text/html");

		PrintWriter out = response.getWriter();

		out.println("<html><title>"+title+"</title><body>" + msg + "!</body></html>");
	}
	
	public static void get200OKJsonObjectResponse(HttpServletResponse response, Object o) throws IOException {
		Gson gson = new Gson();
		get200OKJsonStringResponse(response, gson.toJson(o));
	}
	
	
	public static void get200OKJsonStringResponse(HttpServletResponse response, String json) throws IOException {
		response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_OK);
		PrintWriter out = response.getWriter();
		out.println(json);
	}
}
