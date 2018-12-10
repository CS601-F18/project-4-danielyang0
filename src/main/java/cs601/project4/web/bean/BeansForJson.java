package cs601.project4.web.bean;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import cs601.project4.bean.Event;
import cs601.project4.web.bean.BeansForJson.UserDetails;

/**
 * helper classes for converting between objects and json for request boyd and response body
 * @author yangzun
 *
 */
public class BeansForJson {
	
	public static class CreateUserRequestInfo {
		private String username;
		public CreateUserRequestInfo(String username) {
			super();
			this.username = username;
		}
		public String getUsername() {
			return username;
		}
	}
	
	
	
	public static class AddTicketsToUserRequestInfo {
		private int eventid;
		private int tickets;
		public AddTicketsToUserRequestInfo(int eventid, int tickets) {
			super();
			this.eventid = eventid;
			this.tickets = tickets;
		}
		public int getEventid() {
			return eventid;
		}
		public int getTickets() {
			return tickets;
		}
	}
	public static class TransferTicketsRequestInfo {
		private int eventid;
		private int tickets;
		private int targetuser;
		public TransferTicketsRequestInfo(int eventid, int tickets, int targetuser) {
			super();
			this.eventid = eventid;
			this.tickets = tickets;
			this.targetuser = targetuser;
		}
		public int getEventid() {
			return eventid;
		}
		public int getTickets() {
			return tickets;
		}
		public int getTargetuser() {
			return targetuser;
		}
	}
	
	public static class CreateEventRequestInfo {
		private int userid;
		private String eventname;
		private int numtickets;
		public CreateEventRequestInfo(int userid, int numtickets) {
			super();
			this.userid = userid;
			this.numtickets = numtickets;
		}
		public int getUserid() {
			return userid;
		}
		public String getEventname() {
			return eventname;
		}
		public int getNumtickets() {
			return numtickets;
		}
	}
	
	public static class PurchaseTicketRequestInfo {
		private int userid;
		private int eventid;
		private int tickets;
		public PurchaseTicketRequestInfo(int userid, int eventid, int tickets) {
			super();
			this.userid = userid;
			this.eventid = eventid;
			this.tickets = tickets;
		}
		public int getUserid() {
			return userid;
		}
		public int getEventid() {
			return eventid;
		}
		public int getTickets() {
			return tickets;
		}
	}
	
	public static class UserDetails {
		private int userid;
		private String username;
		private List<EventId> tickets;
		public UserDetails(int userid, String username, List<EventId> tickets) {
			super();
			this.userid = userid;
			this.username = username;
			this.tickets = tickets;
		}
		public int getUserid() {
			return userid;
		}
		public String getUsername() {
			return username;
		}
		public List<EventId> getTickets() {
			return tickets;
		}
	}
	
	
	public static class EventId {
		private int eventid;
		public EventId(int eventid) {
			this.eventid = eventid;
		}
		public int getEventid() {
			return eventid;
		}
		
	}
	
	public static class GetMultipleEventsRequestInfo {
		private List<EventId> data;
		public GetMultipleEventsRequestInfo(List<EventId> data) {
			super();
			this.data = data;
		}
		public List<EventId> getData() {
			return data;
		}
	}

	
	/**
	 * FrontEnd Service:  GET /users/{userid} response
	 * @author yangzun
	 *
	 */
	public static class UserDetailsEventDetails {
		public int userid;
		public String username;
		public List<EventDetail> tickets;
		public UserDetailsEventDetails(int userid, String username, List<EventDetail> tickets) {
			super();
			this.userid = userid;
			this.username = username;
			this.tickets = tickets;
		}
	}
	
	/**
	 * FrontEnd Service: ET /users/{userid} response tickets
	 * @author yangzun
	 *
	 */
	public static class EventDetail {
		private int eventid;
		private String eventname;
		private int userid;
		private int avail;
		private int purchased;
		public EventDetail(int eventid, String eventname, int userid, int avail, int purchased) {
			super();
			this.eventid = eventid;
			this.eventname = eventname;
			this.userid = userid;
			this.avail = avail;
			this.purchased = purchased;
		}
		public EventDetail(Event e) {
			this(e.getId(), e.getName(), e.getUserid(), e.getAvail(), e.getPurchased());
		}
		
		
	}
	
	public static void main(String[] args) {
		
		Gson gson = new Gson();
		ArrayList<EventId> lst = new ArrayList<EventId>();
		lst.add(new EventId(1));
		lst.add(new EventId(2));
		lst.add(new EventId(3));
		UserDetails userDetails = new UserDetails(1,"woaini",lst);
		String js = gson.toJson(userDetails);
		System.out.println(js);
		
		String js2 = "{\"userid\":1,\"username\":\"woaini\",\"tickets\":[{\"eventid\":1},{\"eventid\":2}]}";
		System.out.println(js2);
		UserDetails fromJson = gson.fromJson(js, UserDetails.class);
		System.out.println(fromJson.getUsername());
		System.out.println(fromJson.getUserid());
		System.out.println(fromJson.getTickets());
		
	}
}
