package cs601.project4.web.eventservice;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import cs601.project4.bean.Event;
import cs601.project4.dao.dbtools.DbHelper;
import cs601.project4.exception.ParamParseException;
import cs601.project4.exception.ServiceException;
import cs601.project4.service.EventService;
import cs601.project4.service.EventServiceImpl;
import cs601.project4.service.ServiceProxy;
import cs601.project4.web.FormatedResponse;
import cs601.project4.web.ParamParser;


public class EventServlet extends HttpServlet {
	private Logger logger = Logger.getLogger(EventServlet.class.getName());

	/**
	 * handles request: GET /list, GET /{eventid}
	 * 
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			ParamParser.parsePath(request, "/list");
			//GET /list
			doListEvents(response);
			return;
		} catch (ParamParseException e) {
		}
		try {
			Map<String, Object> parsed = ParamParser.parsePath(request, "/{eventid:int}");
			int eventid = (Integer) parsed.get("eventid");
			//GET /{eventid}
			doGetOneEvent(response, eventid);
			return;
		} catch (ParamParseException e) {
		}
		FormatedResponse.get400Response(response, "unknown request", "unknown request");
	}

	/**
	 * GET /events
	 * @param response
	 * @throws IOException
	 */
	private void doListEvents(HttpServletResponse response) throws IOException {
		EventService es = ServiceProxy.getProxy(EventService.class, new EventServiceImpl());
		List<Event> events = null;
		try {
			events = es.listEvents();
		} catch (SQLException e) {
		}
		if(events == null) {
			FormatedResponse.get400Response(response, "Events not found", "Events not found");
			return;
		}
		FormatedResponse.get200OKJsonObjectResponse(response, events);
	}
	
	private void doGetOneEvent(HttpServletResponse response, int eventid) throws IOException {
		EventService es = ServiceProxy.getProxy(EventService.class, new EventServiceImpl());
		Event event = null;
		try {
			event = es.getEvent(eventid);
		} catch (SQLException e) {
		}
		if(event == null) {
			FormatedResponse.get400Response(response, "Event not found", "Event not found");
			return;
		}
		FormatedResponse.get200OKJsonObjectResponse(response, event);
	}

	/**
	 * POST /create,   POST /purchase/{eventid}
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		EventService es = ServiceProxy.getProxy(EventService.class, new EventServiceImpl());
		try {
			ParamParser.parsePath(request, "/create");
			//POST /create
			int[] parseIntParams = ParamParser.parseIntParams(request, new String[]{"userid", "numtickets"});
			int userid = parseIntParams[0];
			int numtickets = parseIntParams[1];
			String[] parseIntParams2 = ParamParser.parseStringParams(request, new String[]{"eventname"});
			String eventname = parseIntParams2[0];
			try {
				int newEventid = es.createEvent(userid, eventname, numtickets);
				FormatedResponse.get200OKJsonStringResponse(response, "{\"eventid\":"+newEventid+"}");
			} catch (SQLException | ServiceException e) {
				FormatedResponse.get400Response(response, "Event unsuccessfully created", "Event unsuccessfully created");
			}
			return;
		} catch (ParamParseException e) {
		}
		try {
			Map<String, Object> parsed = ParamParser.parsePath(request, "/purchase/{eventid:int}");
			//POST /purchase/{eventid}
			int eventid = (Integer) parsed.get("eventid");
			try {
				int[] parseIntParams = ParamParser.parseIntParams(request, new String[]{"userid", "eventid", "tickets"});
				int userid = parseIntParams[0];
				if(eventid != parseIntParams[1]) {
					throw new ParamParseException("eventid not correct");
				}
				int tickets = parseIntParams[2];
				System.out.println("-----post purchase: " + userid+" "+ eventid + " " + tickets);
			} catch (ParamParseException e) {
				// 404
				FormatedResponse.get400Response(response, "Bad Request", "Bad Request");
			}
			return;
		} catch (ParamParseException e) {
		}
		//404
		FormatedResponse.get400Response(response, "Bad Request", "Bad Request");
	}

}
