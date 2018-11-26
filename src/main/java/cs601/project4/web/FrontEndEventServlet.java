package cs601.project4.web;

import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;

import cs601.project4.bean.Event;
import cs601.project4.service.EventService;
import cs601.project4.service.EventServiceImpl;
import cs601.project4.service.ServiceProxy;


public class FrontEndEventServlet extends HttpServlet {
	Connection conn = null;
	//	private static Logger logger = Logger.getLogger(EventServlet.class);
	//	static {
	//		PropertyConfigurator.configure("./config/log4j.properties");
	//	}
	private Logger logger = Logger.getLogger(FrontEndEventServlet.class.getName());

	/**
	 * handles request: GET /events, GET /events/{eventid}
	 * 
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

		String requestURI = request.getRequestURI();
		logger.info(requestURI);
		String[] splited = requestURI.split("/");
		if(splited.length == 2) {
			//GET /events
			doGetEvents(response);
		}else if(splited.length == 3) {
			//GET /events/{eventid}
			int eventid = 0;
			try {
				eventid = Integer.valueOf(splited[2]);
			} catch (NumberFormatException e) {
				logger.info("path: "+requestURI+" : eventid is not an integer");
				FormatedResponse.get400Response(response, "event id error", "event id is not an integer");
				return;
			}
			doGetOneEvent(response, eventid);
		}else {
			logger.info("path: "+requestURI+" : unknown request");
			FormatedResponse.get400Response(response, "unknown request", "unknown request");
		}
	}

	
	private String requestOneEvent(int eventid) throws IOException {
		URL url = new URL("http://localhost:8081/"+eventid);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("GET");
		int status = con.getResponseCode();
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuilder content = new StringBuilder();
		while ((inputLine = in.readLine()) != null) {
			content.append(inputLine).append("\n");
		}
		in.close();
		return content.toString();
	}
	
	/**
	 * call Event Service API  GET /list
	 * @throws IOException
	 */
	private String requestListEvents() throws IOException {
		URL url = new URL("http://localhost:8081/list");
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("GET");
		int status = con.getResponseCode();
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuilder content = new StringBuilder();
		while ((inputLine = in.readLine()) != null) {
			content.append(inputLine).append("\n");
		}
		in.close();
//		logger.info(status+"");
//		logger.info(content.toString());
		return content.toString();
	}

	/**
	 * GET /events
	 * @param response
	 * @throws IOException
	 */
	private void doGetEvents(HttpServletResponse response) throws IOException {
		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);
		PrintWriter out = response.getWriter();
		StringBuffer sb = new StringBuffer("<html><title>FrontEndEventServlet</title><body>");
		Gson gson = new Gson();
		List<Event> events = null;
		//call Event Service API
		String listEvents = requestListEvents();
		//https://stackoverflow.com/questions/5554217/google-gson-deserialize-listclass-object-generic-type
		Type listType = new TypeToken<ArrayList<Event>>(){}.getType();
		try {
			events = gson.fromJson(listEvents, listType);
		} catch (JsonParseException e) {
			logger.info("json parse error");
		}
		if(events == null || events.size() == 0) {
			FormatedResponse.get400Response(response, "no events found", "no events found");
			return;
		}
		events.forEach(e ->{ sb.append(e.getName()+":"+e.getPurchased()+"<br/>");});
		sb.append("</body></html>");
		out.println(sb);
	}

	private void doGetOneEvent(HttpServletResponse response, int eventid) throws IOException {
		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);
		PrintWriter out = response.getWriter();
		StringBuffer sb = new StringBuffer("<html><title>FrontEndEventServlet</title><body>");
		Gson gson = new Gson();
		Event event = null;
		//call Event Service API
		String eventString = requestOneEvent(eventid);
		try {
			event = gson.fromJson(eventString, Event.class);
		} catch (JsonParseException e) {
			logger.info("json parse error");
		}
		if(event == null) {
			FormatedResponse.get400Response(response, "no events found", "no events found");
			return;
		}
		sb.append(event.getName()+":"+event.getPurchased()+"<br/>");
		sb.append("</body></html>");
		out.println(sb);
	}


	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

		//POST /
		String requestURI = request.getRequestURI();
		logger.info(requestURI);
		String[] splited = requestURI.split("/");
		if(splited.length == 3 && "create".equals(splited[2])) {
			//POST /events/create
		}else if(splited.length == 5 && "purchase".equals(splited[3])){
			//POST /events/{eventid}/purchase/{userid}
			int eventid = 0;
			try {
				eventid = Integer.valueOf(splited[2]);
			} catch (NumberFormatException e) {
				// TODO: handle exception
			}
			int userid = 0;
			try {
				userid = Integer.valueOf(splited[4]);
			} catch (NumberFormatException e) {
				// TODO: handle exception
			}
		}else{
			//unsupported path
		}
		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);

		PrintWriter out = response.getWriter();

		String msg = request.getParameter("usermsg");

		out.println("<html><title>EchoServlet</title><body>You said: " + msg + "</body></html>");

	}

}
