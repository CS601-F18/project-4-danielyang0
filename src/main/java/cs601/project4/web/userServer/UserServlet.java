package cs601.project4.web.userServer;

import java.io.IOException;
import java.io.PrintWriter;
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
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;

import cs601.project4.bean.Event;
import cs601.project4.bean.Ticket;
import cs601.project4.bean.User;
import cs601.project4.dao.dbtools.DbHelper;
import cs601.project4.dbservice.EventDBService;
import cs601.project4.dbservice.EventDBServiceImpl;
import cs601.project4.dbservice.TicketDBService;
import cs601.project4.dbservice.TicketDBServiceImpl;
import cs601.project4.dbservice.UserDBService;
import cs601.project4.dbservice.UserDBServiceImpl;
import cs601.project4.dbservice.DBServiceProxy;
import cs601.project4.exception.ParamParseException;
import cs601.project4.exception.ServiceException;
import cs601.project4.web.FormatedResponse;
import cs601.project4.web.ParamParser;
import cs601.project4.web.bean.BeansForJson;
import cs601.project4.web.bean.BeansForJson.AddTicketsToUserRequestInfo;
import cs601.project4.web.bean.BeansForJson.TransferTicketsRequestInfo;

/**
 * 
 * User Service - The user service will manage the user account information, 
 * including the events for which a user has purchased tickets. 
 * The API will support the following operations:
 * 
 * 1. Create a new user
 * 2. Get user details
 * 3. Add a new ticket for a user
 * 4. Transfer tickets from one user to another
 * @author yangzun
 *
 */
public class UserServlet extends HttpServlet {
	private Logger logger = Logger.getLogger(UserServlet.class.getName());
	private UserDBService us = DBServiceProxy.getProxy(UserDBService.class, new UserDBServiceImpl());
	private TicketDBService ts = DBServiceProxy.getProxy(TicketDBService.class, new TicketDBServiceImpl());

	/**
	 * 2.Get user details
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Map<String, Object> parsed = new HashMap<>();
		if( ParamParser.parsePath(request, "/{userid:int}", parsed) ){
			int userid = (Integer) parsed.get("userid");
			//2.Get user details  GET /{userid}
			getUserDetail(response, userid);
		}else{
			FormatedResponse.get400Response(response, "unknown request", "unknown request");
		}
	}
	
	/**
	 * 2. Get user details GET/{userid}
	 * -> {"userid": 0,"username": "string","tickets": [{"eventid": 0}]} || User not found
	 * @param response
	 * @param userid
	 * @throws IOException
	 */
	private void getUserDetail(HttpServletResponse response, int userid) throws IOException {
		User user = null;
		List<Ticket> tickets = null;
		try {
			user = us.getUserById(userid);
			if(user != null) {
				tickets = ts.getUserTickets(userid);
			}
		} catch (SQLException e) {
		}
		if(user == null) {
			//TODO
			FormatedResponse.get400Response(response, "User not found", "User not found");
			return;
		}
		List<BeansForJson.EventId> eventIds = new ArrayList<>();
		if(tickets != null) {
			tickets.forEach((e) -> {eventIds.add(new BeansForJson.EventId(e.getEventid()));});
		}
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("userid",userid);
		jsonObject.addProperty("username",user.getName());
		Gson gson = new Gson();
		JsonElement element = gson.toJsonTree(eventIds, new TypeToken<List<BeansForJson.EventId>>(){}.getType());
//		if(element.isJsonArray()) {
//			JsonArray jsonArray = element.getAsJsonArray();
//		}
		jsonObject.add("tickets", element);
		FormatedResponse.get200OKJsonStringResponse(response, jsonObject.toString());
	}

	/**
	 * 1. Create a new user
	 * 3. Add a new ticket for a user
	 * 4. Transfer tickets from one user to another
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Map<String, Object> parsed = new HashMap<>();
		if(ParamParser.parsePath(request, "/create", parsed)) {
			//1.Create a new user
			//POST /create
			createUser(request, response);
		}else if(ParamParser.parsePath(request, "/{userid:int}/tickets/add",parsed)) {
			//3. Add a new ticket for a user
			//POST /{userid}/tickets/add
			addTicketsToUser(request, response, parsed);
		}else if(ParamParser.parsePath(request, "/{userid:int}/tickets/transfer",parsed)) {
			//4. Transfer tickets from one user to another
			//POST /{userid}/tickets/transfer
			transferTickets(request, response, parsed);
		}else{
			FormatedResponse.get400Response(response, "Bad Request", "Bad Request");
		}
	}

	/**
	 * 3. Add a new ticket for a user
	 * POST /{userid}/tickets/add
	 * @param request
	 * @param response
	 * @param parsed
	 * @throws IOException
	 */
	private void addTicketsToUser(HttpServletRequest request, HttpServletResponse response,
			Map<String, Object> parsed) throws IOException {
		int userid = (Integer) parsed.get("userid");
		try {
			AddTicketsToUserRequestInfo postObject = ParamParser.parseJsonToObject(request, BeansForJson.AddTicketsToUserRequestInfo.class);
			int eventid = postObject.getEventid();
			int tickets = postObject.getTickets();
			boolean success = ts.addTicketsToUserIfExists(userid, eventid, tickets);
			if(success) {
				JsonObject jsonObject = new JsonObject();
				jsonObject.addProperty("OK", "Event tickets added");
				FormatedResponse.get200OKJsonStringResponse(response, jsonObject.toString());
			}else{
				FormatedResponse.get400Response(response, "Bad Request", "Bad Request");
			}
		} catch (ParamParseException | SQLException e) {
			// 404
			FormatedResponse.get400Response(response, "Bad Request", "Bad Request");
		}
	}
	
	/**
	 * 4. Transfer tickets from one user to another
	 * POST /{userid}/tickets/transfer
	 * @param request
	 * @param response
	 * @param parsed
	 * @throws IOException
	 */
	private void transferTickets(HttpServletRequest request, HttpServletResponse response,
			Map<String, Object> parsed) throws IOException {
		int userid = (Integer) parsed.get("userid");
		try {
			TransferTicketsRequestInfo postObject = ParamParser.parseJsonToObject(request, BeansForJson.TransferTicketsRequestInfo.class);
			int eventid = postObject.getEventid();
			int tickets = postObject.getTickets();
			int targetUserId = postObject.getTargetuser();
			boolean success = ts.transferTickets(userid, targetUserId, eventid, tickets);
			if(success) {
				JsonObject jsonObject = new JsonObject();
				jsonObject.addProperty("OK", "Event tickets transfered");
				FormatedResponse.get200OKJsonStringResponse(response, jsonObject.toString());
			}else{
				FormatedResponse.get400Response(response, "Bad Request", "Bad Request");
			}
		} catch (ParamParseException | SQLException | ServiceException e) {
			// 404
			FormatedResponse.get400Response(response, "Bad Request", "Bad Request");
		}
	}
	
	
	/**
	 * 1. Create a new user
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	private void createUser(HttpServletRequest request, HttpServletResponse response) throws IOException{
		try {
			String username = ParamParser.parseJsonToObject(request, BeansForJson.CreateUserRequestInfo.class).getUsername();
			if(username == null) {
				return;
			}
			try {
				int newUserId = us.createUser(username);//createEvent(userid, eventname, numtickets);
				//TODO
				if(newUserId == 0) {
					FormatedResponse.get400Response(response, "User unsuccessfully created", "User unsuccessfully created");
					return;
				}
				JsonObject jsonObject = new JsonObject();
				jsonObject.addProperty("userid", newUserId);
				FormatedResponse.get200OKJsonStringResponse(response, jsonObject.toString());
			} catch (SQLException e) {
				FormatedResponse.get400Response(response, "User unsuccessfully created", "User unsuccessfully created");
			}
		} catch (ParamParseException e) {
			// TODO: handle exception
			// handle parameter error
		}
	}

}

