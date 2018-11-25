package cs601.project4.service;

import java.sql.SQLException;
import java.util.List;
import cs601.project4.bean.Event;
import cs601.project4.dao.EventDAO;
import cs601.project4.dao.TicketDAO;
import cs601.project4.dao.UserDAO;
import cs601.project4.dao.dbtools.DbHelper;
import cs601.project4.exception.ServiceException;
/**
 * Service class for operating table event
 * @author yangzun
 *
 */
public class EventServiceImpl implements EventService {
	private EventDAO eventDAO = new EventDAO();
	
	/**
	 * create an event: POST /create {"userid": 0,"eventname": "string","numtickets": 0} 
	 * -> {"eventid": 0 } || false
	 * @param userid
	 * @param eventName
	 * @param numTickets
	 * @throws SQLException
	 */
	@Override
	public int createEvent(int userid, String eventName, int numTickets) throws SQLException {
		Event event = new Event();
		event.setUserid(userid);
		event.setName(eventName);
		event.setAvail(numTickets);
		event.setPurchased(0);
		eventDAO.addEvent(event);
		int newEventId = DbHelper.getLastIncreasedID();
		return newEventId;
	}
	
	/**
	 * get all the events in the db
	 * @return
	 * @throws SQLException
	 */
	public List<Event> listEvents() throws SQLException {
		return eventDAO.getEvents();
	}
	
	/**
	 * get event according to event id
	 * @param id
	 * @return
	 * @throws SQLException
	 */
	public Event getEvent(int id) throws SQLException {
		return eventDAO.getEventById(id);
	}
	
	/**
	 * POST /purchase/{eventid}    {"userid": 0,"eventid": 0,"tickets": 0} -> Event tickets purchased || Tickets could not be purchased
	 * @throws SQLException 
	 */
	@Override
	public boolean purchase(int userid, int eventid, int tickets) throws SQLException {
		if(!isUserExisted(userid)) {
			throw new ServiceException("User not found");
		}
		if(eventDAO.getEventById(eventid) == null) {
			throw new ServiceException("Event not found");
		}
		int rowsAffected = eventDAO.decreaseAvail(eventid, tickets);
		if(rowsAffected == 0) {
			throw new ServiceException("Available tickets are not enough");
		}
		return increaseUserTicket(userid, eventid, tickets);
	}
	
	//TO DO: call User Service API 
	private boolean isUserExisted(int userid) {
		
		UserDAO userDAO = new UserDAO();
		try {
			return userDAO.getUserById(userid) == null;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	//TO DO: call User Service API
	private boolean increaseUserTicket(int userid, int eventid, int tikcets) {
		TicketDAO ticketDAO = new TicketDAO();
		try {
			boolean success = ticketDAO.increaseTicketsForUser(userid, eventid, tikcets);
			if(success) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * decrease the available ticket number of an event
	 * @param eventid
	 * @param tickets
	 * @return
	 * @throws SQLException
	 */
	boolean decreaseTicket(int eventid, int tickets) throws SQLException {
		int rowsAffected = eventDAO.decreaseAvail(eventid, tickets);
		if(rowsAffected == 0 ) {
			return false;
		}
		return true;
	}
}
