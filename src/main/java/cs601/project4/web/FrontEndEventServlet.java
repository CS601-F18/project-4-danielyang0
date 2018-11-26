package cs601.project4.web;

import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import cs601.project4.exception.ParamParseException;
import cs601.project4.service.EventService;
import cs601.project4.service.EventServiceImpl;
import cs601.project4.service.ServiceProxy;


public class FrontEndEventServlet extends HttpServlet {
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
		try {
			ParamParser.parsePath(request, "/events");
			doGetEvents(response);
			return;
		} catch (ParamParseException e) {
		}
		try {
			Map<String, Object> parsed = ParamParser.parsePath(request, "/events/{eventid:int}");
			int eventid = (Integer) parsed.get("eventid");
			doGetOneEvent(response, eventid);
			return;
		} catch (ParamParseException e) {
		}
		FormatedResponse.get400Response(response, "Bad request", "Bad request");
	}
	
	/**
	 * GET /events
	 * @param response
	 * @throws IOException
	 */
	private void doGetEvents(HttpServletResponse response) throws IOException {
		List<Event> events = null;
		//call Event Service API
		Map<String, String> listEvents = null;
		try {
			listEvents = MyHttpClient.fetchGet("http://localhost:8081/list");
		} catch (IOException e) {
			FormatedResponse.get400Response(response, "no events found", "no events found");
			return;
		}
		if(listEvents.get("status").equals("200")) {
			FormatedResponse.get200OKJsonStringResponse(response, listEvents.get("content"));
		}else{
			FormatedResponse.get400Response(response, "no events found", "no events found");
		}
		
		//https://stackoverflow.com/questions/5554217/google-gson-deserialize-listclass-object-generic-type
//		Gson gson = new Gson();
//		Type listType = new TypeToken<ArrayList<Event>>(){}.getType();
//		try {
//			events = gson.fromJson(listEvents, listType);
//		} catch (JsonParseException e) {
//			logger.info("json parse error");
//		}
//		if(events == null || events.size() == 0) {
//			FormatedResponse.get400Response(response, "no events found", "no events found");
//			return;
//		}
//		events.forEach(e ->{ sb.append(e.getName()+":"+e.getPurchased()+"<br/>");});
//		sb.append("</body></html>");
//		out.println(sb);
	}

	private void doGetOneEvent(HttpServletResponse response, int eventid) throws IOException {
		List<Event> event = null;
		//call Event Service API
		Map<String, String> eventResult = null;
		try {
			eventResult = MyHttpClient.fetchGet("http://localhost:8081/"+eventid);
		} catch (Exception e) {
			FormatedResponse.get400Response(response, "no events found", "no events found");
			return;
		}
		if(eventResult.get("status").equals("200")) {
			FormatedResponse.get200OKJsonStringResponse(response, eventResult.get("content"));
		}else{
			FormatedResponse.get400Response(response, "no events found", "no events found");
		}
	}


	/**
	 * POST /events/create, POST /events/{eventid}/purchase/{userid}
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			ParamParser.parsePath(request, "/events/create");
			int[] vals = ParamParser.parseIntParams(request, new String[]{"userid", "numtickets"} );
			int userid = vals[0];
			int numtickets = vals[1];
			String eventname = ParamParser.parseStringParams(request, new String[]{"eventname"} )[0];
			doCreate(response, userid, eventname, numtickets);
			return;
		} catch (ParamParseException e) {
		}
		try {
			Map<String, Object> parsed = ParamParser.parsePath(request, "/events/{eventid:int}/purchase/{userid:int}");
			int eventid = (Integer) parsed.get("eventid");
			int userid = (Integer) parsed.get("userid");
			int numtickets = ParamParser.parseIntParams(request, new String[]{"tickets"} )[0];
			doPurchase(response, eventid, userid, numtickets);
			return;
		} catch (ParamParseException e) {
		}
		FormatedResponse.get400Response(response, "Bad request", "Bad request");
	}
	
	private void doCreate(HttpServletResponse response, int userid, String eventname, int numtickets) throws IOException {
		Map<String, String> createPostData = new HashMap<>();
		createPostData.put("userid", ""+userid);
		createPostData.put("eventname", eventname);
		createPostData.put("numtickets", ""+numtickets);
		Map<String, String> eventCreateResult = MyHttpClient.fetchPost("http://localhost:8081/create", createPostData);
		String status = eventCreateResult.get("status");
		if("200".equals(status)) {
			String content = eventCreateResult.get("content");
			FormatedResponse.get200OKJsonStringResponse(response, content);
		}else if("400".equals(status)) {
			FormatedResponse.get400Response(response, "Event unsuccessfully created", "Event unsuccessfully created");
		}
	}

	private void doPurchase(HttpServletResponse response, int eventid, int userid, int numtickets) {
		// TODO Auto-generated method stub
		
	}

}
