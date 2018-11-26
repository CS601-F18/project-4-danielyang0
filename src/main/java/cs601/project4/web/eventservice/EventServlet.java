package cs601.project4.web.eventservice;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import cs601.project4.bean.Event;
import cs601.project4.dao.dbtools.DbHelper;
import cs601.project4.service.EventService;
import cs601.project4.service.EventServiceImpl;
import cs601.project4.service.ServiceProxy;
import cs601.project4.web.FormatedResponse;


public class EventServlet extends HttpServlet {
	private Logger logger = Logger.getLogger(EventServlet.class.getName());

	/**
	 * handles request: GET /list, GET /{eventid}
	 * 
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String requestURI = request.getRequestURI();
		logger.info(requestURI);
		String[] splited = requestURI.split("/");
		if(splited.length == 2) {
			//GET /list
			if("list".equals(splited[1])) {
				doListEvents(response);
			}else {
				int eventid = 0;
				try {
					eventid = Integer.valueOf(splited[1]);
				} catch (NumberFormatException e) {
					logger.info("path: "+requestURI+" : eventid is not an integer");
					FormatedResponse.get400Response(response, "event id error", "event id is not an integer");
					return;
				}
				doGetOneEvent(response, eventid);
			}
		}else {
			logger.info("path: "+requestURI+" : unknown request");
			FormatedResponse.get400Response(response, "unknown request", "unknown request");
		}
	}

	/**
	 * GET /events
	 * @param response
	 * @throws IOException
	 */
	private void doListEvents(HttpServletResponse response) throws IOException {
		response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_OK);
		PrintWriter out = response.getWriter();
		EventService es = ServiceProxy.getProxy(EventService.class, new EventServiceImpl());
		Gson gson = new Gson();
		try {
			List<Event> events = es.listEvents();
			out.println(gson.toJson(events));
		} catch (SQLException e) {
			out.println(gson.toJson(null));
			return;
		}
	}
	
	private void doGetOneEvent(HttpServletResponse response, int eventid) throws IOException {
		response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_OK);
		PrintWriter out = response.getWriter();
		EventService es = ServiceProxy.getProxy(EventService.class, new EventServiceImpl());
		Gson gson = new Gson();
		try {
			Event event = es.getEvent(eventid);
			out.println(gson.toJson(event));
		} catch (SQLException e) {
			out.println(gson.toJson(null));
			return;
		}
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
