package cs601.project4.service;

import java.sql.SQLException;
import java.util.List;
import cs601.project4.bean.Event;
import cs601.project4.dao.EventDAO;

/**
 * Service class for operating table event
 * @author yangzun
 *
 */
public class EventService {
	private EventDAO eventDAO = new EventDAO();
	/**
	 * create an event 
	 * @param userid
	 * @param eventName
	 * @param numTickets
	 * @throws SQLException
	 */
	public void createEvent(int userid, String eventName, int numTickets) throws SQLException {
		Event event = new Event();
		event.setUserid(userid);
		event.setName(eventName);
		event.setAvail(numTickets);
		event.setPurchased(0);
		eventDAO.addEvent(event);
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
	 * decrease the available ticket number of an event
	 * @param eventid
	 * @param tickets
	 * @return
	 * @throws SQLException
	 */
	public boolean decreaseTicket(int eventid, int tickets) throws SQLException {
		int rowsAffected = eventDAO.decreaseAvail(eventid, tickets);
		if(rowsAffected == 0 ) {
			return false;
		}
		return true;
	}
}
