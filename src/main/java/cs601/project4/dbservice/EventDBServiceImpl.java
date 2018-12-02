package cs601.project4.dbservice;

import java.sql.SQLException;
import java.util.ArrayList;
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
public class EventDBServiceImpl implements EventDBService {
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
	@Override
	public List<Event> listEvents() throws SQLException {
		return eventDAO.getEvents();
	}

	/**
	 * get event according to event id
	 * @param id
	 * @return
	 * @throws SQLException
	 */
	@Override
	public Event getEvent(int id) throws SQLException {
		return eventDAO.getEventById(id);
	}

	@Override
	public List<Event> getMultipleEvents(List<Integer> ids) throws SQLException {
		List<Event> events = new ArrayList<>();
		for (Integer id : ids) {
			if(id == null) continue;
			Event event = eventDAO.getEventById(id);
			if(event != null) {
				events.add(event); 
			}
		}
		return events;
	}

	/**
	 * POST /purchase/{eventid}    {"userid": 0,"eventid": 0,"tickets": 0} -> Event tickets purchased || Tickets could not be purchased
	 * @throws SQLException 
	 */
	@Override
	public void purchase(int eventid, int tickets) throws SQLException {
		if(eventDAO.getEventById(eventid) == null) {
			throw new ServiceException("Event not found");
		}
		//		if(!isUserExisted(userid)) {
		//			throw new ServiceException("User not found");
		//		}
		int rowsAffected = eventDAO.decreaseAvail(eventid, tickets);
		if(rowsAffected == 0) {
			throw new ServiceException("Available tickets are not enough");
		}
		//		return increaseUserTicket(userid, eventid, tickets);
	}

	@Override
	public void increaseAvailTickets(int eventid, int tickets) throws SQLException {
		int rowsAffected = eventDAO.increaseAvail(eventid, tickets);
		if(rowsAffected == 0) {
			throw new ServiceException("Available tickets are not enough");
		}
	}

	//TO DO: call User Service API 
	//	private boolean isUserExisted(int userid) {
	//		
	//		UserDAO userDAO = new UserDAO();
	//		try {
	//			return userDAO.getUserById(userid) != null;
	//		} catch (SQLException e) {
	//			e.printStackTrace();
	//		}
	//		return false;
	//	}

	//TO DO: call User Service API
	//	private boolean increaseUserTicket(int userid, int eventid, int tikcets) {
	//		TicketDAO ticketDAO = new TicketDAO();
	//		try {
	//			boolean success = ticketDAO.increaseTicketsForUser(userid, eventid, tikcets);
	//			if(success) {
	//				return true;
	//			}
	//		} catch (SQLException e) {
	//			e.printStackTrace();
	//		}
	//		return false;
	//	}

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
