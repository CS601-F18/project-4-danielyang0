package cs601.project4.web.eventServer;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import cs601.project4.bean.Event;
import cs601.project4.dao.dbtools.DbHelper;
import cs601.project4.dbservice.EventDBService;
import cs601.project4.dbservice.EventDBServiceImpl;
import cs601.project4.dbservice.DBServiceProxy;
import cs601.project4.exception.ParamParseException;
import cs601.project4.exception.ServiceException;
import cs601.project4.tools.PropertyReader;
import cs601.project4.web.FormatedResponse;
import cs601.project4.web.MyHttpClient;
import cs601.project4.web.ParamParser;
import cs601.project4.web.bean.BeansForJson;
import cs601.project4.web.bean.BeansForJson.CreateEventRequestInfo;
import cs601.project4.web.bean.BeansForJson.EventDetail;
import cs601.project4.web.bean.BeansForJson.PurchaseTicketRequestInfo;
import cs601.project4.web.bean.BeansForJson.UserDetails;

/**
 * The event service will manage the list of events and the number of tickets 
 * sold and available for each. When a ticket is purchased it is the responsibility 
 * of the Event Service to notify the User Service of the user's purchase. 
 * 
 * 1.Create a new event
 * 2.Get a list of all events
 * 3.Get details about a specific event
 * 4.Purchase tickets for an event, updating the user's ticket list
 * @author yangzun
 *
 */
public class EventServlet extends HttpServlet {
	private Logger logger = Logger.getLogger(EventServlet.class.getName());
	private EventDBService es = DBServiceProxy.getProxy(EventDBService.class, new EventDBServiceImpl());
	//	private EventDBService es = DBServiceProxy.getProxy(EventDBService.class, new EventDBServiceImpl());
	private static PropertyReader reader = new PropertyReader("./config","eventServer.properties");
	/**
	 * 2.Get a list of all events
	 * 3.Get details about a specific event
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Map<String, Object> parsed = new HashMap<>();
		if( ParamParser.parsePath(request, "/list", parsed) ){
			//2.Get a list of all events  GET /list
			doListEvents(response);
		}else if(ParamParser.parsePath(request, "/{eventid:int}", parsed)){
			int eventid = (Integer) parsed.get("eventid");
			//3.Get details about a specific event GET /{eventid}
			doGetOneEvent(response, eventid);
		}else{
			FormatedResponse.get404Response(response);
		}
	}

	/**
	 * handles request: GET /list
	 * 2.Get a list of all events
	 * @param response
	 * @throws IOException
	 */
	private void doListEvents(HttpServletResponse response) throws IOException {
		List<Event> events = null;
		try {
			events = es.listEvents();
		} catch (SQLException e) {
		}
		if(events == null) {
			FormatedResponse.get400Response(response, "Events not found");
			return;
		}
		//http://blog.csdn.net/ioriogami/article/details/12782141/
		List<EventDetail> eventsDetails = events.stream().map(e -> new BeansForJson.EventDetail(e)).collect(Collectors.toList());
		FormatedResponse.get200OKJsonObjectResponse(response, eventsDetails);
	}


	/**
	 * 3.Get details about a specific event GET /{eventid}
	 * @param response
	 * @param eventid
	 * @throws IOException
	 */
	private void doGetOneEvent(HttpServletResponse response, int eventid) throws IOException {
		Event event = null;
		try {
			event = es.getEvent(eventid);
		} catch (SQLException e) {
		}
		if(event == null) {
			FormatedResponse.get400Response(response, "Event not found");
			return;
		}
		EventDetail eventsDetails = new BeansForJson.EventDetail(event);
		FormatedResponse.get200OKJsonObjectResponse(response, eventsDetails);
	}

	/**
	 * additonal: getEventDeitials
	 * POST /listmany 
	 * request body  [{"eventid":1},{"eventid":2}]
	 * response body 
	 * [{
		"eventid": 0, 
		"eventname": "string", 
		"userid": 0,		
		"avail": 0, 
		"purchased": 0
		}]
	 * 
	 * @param response
	 * @param eventid
	 * @throws IOException
	 */
	private void getMultipleEvents(HttpServletRequest request, HttpServletResponse response) throws IOException {
//		List<BeansForJson.EventId> eventids = ParamParser.parseJsonToObject(request, BeansForJson.GetMultipleEventsRequestInfo.class).getData(); 
		List<BeansForJson.EventId> eventids = ParamParser.<List<BeansForJson.EventId>>parseJsonToObject(request, new TypeToken<List<BeansForJson.EventId>>(){}.getType()); 
		if(eventids == null) {
			FormatedResponse.get400Response(response, "Bad Request");
			return;
		}
		List<Integer> ids = new ArrayList<>();
		eventids.forEach( (e) -> {ids.add(e.getEventid());} );
		List<Event> events = null;
		try {
			events = es.getMultipleEvents(ids);
		} catch (SQLException e) {
			events = new ArrayList<>();
		}
		List<BeansForJson.EventDetail> eventDetails = new ArrayList<>();
		events.forEach( (e) -> { 
			eventDetails.add(new BeansForJson.EventDetail(
					e.getId(), e.getName(), e.getUserid(), e.getAvail(), e.getPurchased())
					);  
		});
		//		BeansForJson.UserDetailsEventDetails responseObject = new BeansForJson.UserDetailsEventDetails(userId, userName, eventDetails);
		FormatedResponse.get200OKJsonObjectResponse(response, eventDetails);
	}


	/**
	 * 
	 * POST /create,   POST /purchase/{eventid}
	 */
	/**
	 * 1.Create a new event
	 * 4.Purchase tickets for an event, updating the user's ticket list
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Map<String, Object> parsed = new HashMap<>();
		if(ParamParser.parsePath(request, "/create", parsed)) {
			//1.Create a new event
			//POST /create
			CreateEvent(request, response);
		}else if(ParamParser.parsePath(request, "/purchase/{eventid:int}",parsed)) {
			//4.Purchase tickets for an event, updating the user's ticket lists
			//POST /purchase/{eventid}
			int eventid = (Integer) parsed.get("eventid");
			purchaseTickets(request, response, eventid);
		}else if(ParamParser.parsePath(request, "/group",parsed)) { 
			//additonal: getEventDeitials
			getMultipleEvents(request, response);
		}else{
			FormatedResponse.get404Response(response);
		}
	}

	private void purchaseTickets(HttpServletRequest request, HttpServletResponse response, int eventid) throws IOException {
		PurchaseTicketRequestInfo postObject = ParamParser.parseJsonToObject(request, BeansForJson.PurchaseTicketRequestInfo.class);
		if(postObject == null) {
			FormatedResponse.get400Response(response, "Tickets could not be purchased");
			return;
		}
		int userid = postObject.getUserid();
		if(eventid != postObject.getEventid()) {
			FormatedResponse.get400Response(response, "Tickets could not be purchased");
			return;
		}
		int tickets = postObject.getTickets();
		//should not purchase non-positive number of tickets
		if(tickets <= 0 ) {
			FormatedResponse.get400Response(response, "Tickets could not be purchased");
			return;
		}
		//decrease tickets of an event
		try {
			es.purchase(eventid, tickets);
		} catch (SQLException | ServiceException e1) {
			FormatedResponse.get400Response(response, "Tickets could not be purchased");
			return;
		}
		boolean success = addTicketToUserByAPI(userid, eventid, tickets, response);
		if(success) {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("OK", "Event tickets purchased");
			FormatedResponse.get200OKJsonStringResponse(response, jsonObject.toString());
		}else{
			try {
				es.increaseAvailTickets(eventid, tickets);
			} catch (ServiceException | SQLException e) {
			}
			FormatedResponse.get400Response(response, "Tickets could not be purchased");
		}
	}
	
	private boolean addTicketToUserByAPI(int userid, int eventid, int tickets, HttpServletResponse response) throws IOException {
		Map<String, String> result = null;
		BeansForJson.AddTicketsToUserRequestInfo postData = new BeansForJson.AddTicketsToUserRequestInfo(eventid, tickets);
		try {
			result = MyHttpClient.fetchPostJson(reader.readStringValue("userService") +"/"+userid + "/tickets/add", postData);
		} catch (IOException e) {
			return false;
		}
		if(result.get("status").equals("200")) {
			return true;

		}else{
			return false;
		}
	}
	
	/**
	 * //1.Create a new event
	 * POST /create
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	private void CreateEvent(HttpServletRequest request, HttpServletResponse response) throws IOException {
		CreateEventRequestInfo postObject = ParamParser.parseJsonToObject(request, BeansForJson.CreateEventRequestInfo.class);
		if(postObject == null) {
			FormatedResponse.get400Response(response, "Event unsuccessfully created");
			return;
		}

		int userid = postObject.getUserid();
		int numtickets = postObject.getNumtickets();
		String eventname = postObject.getEventname();
		if(eventname == null || eventname.isEmpty() || numtickets < 0 || !isUserExisted(userid)) {
			FormatedResponse.get400Response(response, "Event unsuccessfully created");
			return;
		}
		try {
			int newEventid = es.createEvent(userid, eventname, numtickets);
			FormatedResponse.get200OKJsonStringResponse(response, "{\"eventid\":"+newEventid+"}");
		} catch (SQLException | ServiceException e) {
			FormatedResponse.get400Response(response, "Event unsuccessfully created");
		}
	}

	private boolean isUserExisted(int userid) {
		Map<String, String> result = null;
		try {
			result = MyHttpClient.fetchGet(reader.readStringValue("userService") +"/exist/"+userid);
		} catch (IOException e) {
			return false;
		}
		return "200".equals(result.get("status"));
	}
	



}
