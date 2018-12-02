package cs601.project4.web.frontEndServer;

import com.google.gson.reflect.TypeToken;

import java.io.IOException;
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
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;

import cs601.project4.bean.Event;
import cs601.project4.dbservice.EventDBService;
import cs601.project4.dbservice.EventDBServiceImpl;
import cs601.project4.dbservice.DBServiceProxy;
import cs601.project4.exception.ParamParseException;
import cs601.project4.web.FormatedResponse;
import cs601.project4.web.MyHttpClient;
import cs601.project4.web.ParamParser;
import cs601.project4.web.bean.BeansForJson;
import cs601.project4.web.bean.BeansForJson.CreateEventRequestInfo;
import cs601.project4.web.bean.BeansForJson.EventDetail;
import cs601.project4.web.bean.BeansForJson.PurchaseTicketRequestInfo;
import cs601.project4.web.bean.BeansForJson.UserDetailsEventDetails;

/**
 * 	 * Web Front End - The web front end will implement an external web service API for the 
	 * application and will support APIs for the following operations:
	 * 1. Get a list of all events
	 * 2. Create a new event
	 * 3. Get details about a specific event
	 * 4. Purchase tickets for an event
	 * 5. Create a user
	 * 6. See a user's information, including details of all events for which the user has purchased tickets
	 * 7. Transfer tickets from one user to another
 * @author yangzun
 *
 */
public class FrontEndServlet extends HttpServlet {
	//	private static Logger logger = Logger.getLogger(EventServlet.class);
	//	static {
	//		PropertyConfigurator.configure("./config/log4j.properties");
	//	}
	private Logger logger = Logger.getLogger(FrontEndServlet.class.getName());

	/**
	 * 1. Get a list of all events
	 * 3. Get details about a specific event
	 * 6. See a user's information, including details of all events for which the user has purchased tickets
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Map<String, Object> parsed = new HashMap<>();
		if(ParamParser.parsePath(request, "/events",parsed)) {
			//1. Get a list of all events
			getEvents(response);
		}else if(ParamParser.parsePath(request, "/events/{eventid:int}",parsed)){
			//3. Get details about a specific event
			int eventid = (Integer) parsed.get("eventid");
			getOneEvent(response, eventid);
		}else if(ParamParser.parsePath(request, "/users/{userid:int}",parsed)){
			//6. See a user's information, including details of all events 
			//for which the user has purchased tickets
			int userid = (Integer) parsed.get("userid");
			doGetUserInformation(response, userid);
		}else{
			FormatedResponse.get400Response(response, "Bad request", "Bad request");
		}
	}

	/**
	 * 1. Get a list of all events
	 * GET /events
	 * @param response
	 * @throws IOException
	 */
	private void getEvents(HttpServletResponse response) throws IOException {
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
	}

	/**
	 * 3. Get details about a specific event
	 * @param response
	 * @param eventid
	 * @throws IOException
	 */
	private void getOneEvent(HttpServletResponse response, int eventid) throws IOException {
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
	 * 6. See a user's information, including details of all events 
	 * /users/{userid}
	 * for which the user has purchased tickets
	 * @param response
	 * @param userid
	 * @throws IOException 
	 */
	private void doGetUserInformation(HttpServletResponse response, int userid) throws IOException {
		Map<String, String> result = null;
		try {
			result = MyHttpClient.fetchGet("http://localhost:8082/" + userid);
		} catch (Exception e) {
			FormatedResponse.get400Response(response, "no User found", "no User found");
			return;
		}
		if(result.get("status").equals("200")) {
			String jsonString = result.get("content");
			BeansForJson.UserDetails userDetails = null;
			Gson gson = new Gson();
			try {
				userDetails = gson.fromJson(jsonString, BeansForJson.UserDetails.class);
			} catch (JsonParseException e) {
				//TODO handle
				logger.info("json parse error");
			}
			List<BeansForJson.EventId> eventids = userDetails.getTickets();
			Map<String, String> result2 = MyHttpClient.fetchPostJson("http://localhost:8081/listmany", eventids);
			
			if(result2.get("status").equals("200")) {
				String jsonString2 = result2.get("content");
				List<BeansForJson.EventDetail> listOfEventDetails = gson.fromJson(jsonString2, new TypeToken<List<BeansForJson.EventDetail>>(){}.getType());
				UserDetailsEventDetails responseObject = new BeansForJson.UserDetailsEventDetails(userid, userDetails.getUsername(), listOfEventDetails);
				
				FormatedResponse.get200OKJsonObjectResponse(response, responseObject);
			}
		}else{
			FormatedResponse.get400Response(response, "no User found", "no User found");
		}
	}


	/**
	 * 2. Create a new event
	 * 4. Purchase tickets for an event
	 * 5. Create a user
	 * 7. Transfer tickets from one user to another
	 * POST /events/create, POST /events/{eventid}/purchase/{userid}
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Map<String, Object> parsed = new HashMap<>();
		if(ParamParser.parsePath(request, "/events/create", parsed)) {
			//2. Create a new event
			CreateEventRequestInfo postObject = ParamParser.parseJsonToObject(request, BeansForJson.CreateEventRequestInfo.class);
			doCreate(response, postObject);
		}else if(ParamParser.parsePath(request, "/events/{eventid:int}/purchase/{userid:int}",parsed)){
			//4. Purchase tickets for an event
			int eventid = (Integer) parsed.get("eventid");
			int userid = (Integer) parsed.get("userid");
			int tickets = ParamParser.parseJsonToObject(request, BeansForJson.PurchaseTicketRequestInfo.class).getTickets();
			doPurchase(response, eventid, userid, tickets);
		}else if(ParamParser.parsePath(request, "/users/create",parsed)){
			//5. Create a user
			String username = ParamParser.parseJsonToObject(request, BeansForJson.CreateUserRequestInfo.class).getUsername();
			createUser(response, username);
		}else if(ParamParser.parsePath(request, "/users/{userid:int}/tickets/transfer", parsed)) {
			//7.Transfer tickets from one user to another
			int userid = (Integer) parsed.get("userid");
			String requestBody = ParamParser.readOriginalBody(request);
			transfer(response, userid, requestBody);
		}else{
			FormatedResponse.get400Response(response, "Bad request", "Bad request");
		}		
	}
	

	/**
	 * 5. Create a user
	 * /users/create
	 * POST /users/create
	 * @param response
	 * @param username
	 * @throws IOException
	 */
	private void createUser(HttpServletResponse response, String username) throws IOException {
		BeansForJson.CreateUserRequestInfo postObject = new BeansForJson.CreateUserRequestInfo(username);
		Map<String, String> result = MyHttpClient.fetchPostJson("http://localhost:8082/create", postObject);
		String status = result.get("status");
		if("200".equals(status)) {
			String content = result.get("content");
			FormatedResponse.get200OKJsonStringResponse(response, content);
		}else if("400".equals(status)) {
			FormatedResponse.get400Response(response, "User unsuccessfully created", "User unsuccessfully created");
		}
	}
	
	/**
	 * //2. Create a new event
	 * POST /events/create
	 * @param response
	 * @param userid
	 * @param eventname
	 * @param numtickets
	 * @throws IOException
	 */
	private void doCreate(HttpServletResponse response, CreateEventRequestInfo object) throws IOException {
		Map<String, String> eventCreateResult = MyHttpClient.fetchPostJson("http://localhost:8081/create", object);
		String status = eventCreateResult.get("status");
		if("200".equals(status)) {
			String content = eventCreateResult.get("content");
			FormatedResponse.get200OKJsonStringResponse(response, content);
		}else if("400".equals(status)) {
			FormatedResponse.get400Response(response, "Event unsuccessfully created", "Event unsuccessfully created");
		}
	}

	/**
	 * //4. Purchase tickets for an event
	 * events/{eventid}/purchase/{userid}
	 * @param response
	 * @param eventid
	 * @param userid
	 * @param numtickets
	 * @throws IOException 
	 */
	private void doPurchase(HttpServletResponse response, int eventid, int userid, int numtickets) throws IOException {
		BeansForJson.PurchaseTicketRequestInfo object = new BeansForJson.PurchaseTicketRequestInfo(userid, eventid, numtickets);
		Map<String, String> result = MyHttpClient.fetchPostJson("http://localhost:8081/purchase/"+eventid, object);
		if(result.get("status").equals("200")) {
			String content = result.get("content");
			FormatedResponse.get200OKJsonStringResponse(response, content);
		}else{
			FormatedResponse.get400Response(response, "fail", "fail");
		}
	}
	
	/**
	 * //7.Transfer tickets from one user to another
	 * /users/{userid}/tickets/transfer
	 * @param response
	 * @param userid
	 * @param requestBody
	 * @throws IOException 
	 */
	private void transfer(HttpServletResponse response, int userid, String requestBody) throws IOException {
		Map<String, String> result = MyHttpClient.fetchPostJsonString("http://localhost:8082/"+userid+"/tickets/transfer", requestBody);
		if(result.get("status").equals("200")) {
			FormatedResponse.get200OKJsonStringResponse(response, result.get("content"));
		}else{
			FormatedResponse.get400Response(response, "fail", "fail");
		}
	}
	
}

