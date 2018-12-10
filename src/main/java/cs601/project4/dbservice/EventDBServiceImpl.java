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
	public int createEvent(int userid, String eventName, int numTickets) {
		Event event = new Event();
		event.setUserid(userid);
		event.setName(eventName);
		event.setAvail(numTickets);
		event.setPurchased(0);
		try {
			eventDAO.addEvent(event);
		} catch (SQLException e) {
			throw new ServiceException(e);
		}
		int newEventId = DbHelper.getLastIncreasedID();
		return newEventId;
	}

	/**
	 * get all the events in the table
	 * @return
	 * @throws SQLException
	 */
	@Override
	public List<Event> listEvents() {
		try {
			return eventDAO.getEvents();
		} catch (SQLException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * get event according to event id
	 * @param id
	 * @return
	 * @throws SQLException
	 */
	@Override
	public Event getEvent(int id) {
		try {
			return eventDAO.getEventById(id);
		} catch (SQLException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * given multiple event ids, return the details of those events.
	 */
	@Override
	public List<Event> getMultipleEvents(List<Integer> ids) {
		List<Event> events = new ArrayList<>();
		for (Integer id : ids) {
			if(id == null) continue;
			Event event;
			try {
				event = eventDAO.getEventById(id);
			} catch (SQLException e) {
				throw new ServiceException(e);
			}
			if(event != null) {
				events.add(event); 
			}
		}
		return events;
	}

	/**
	 * decrease the number of avail and purchase tickets of an event
	 * @throws SQLException 
	 */
	@Override
	public void purchase(int eventid, int tickets) {
		Event event = null;
		try {
			event = eventDAO.getEventById(eventid);
		} catch (SQLException e) {
			throw new ServiceException(e);
		}
		if( event == null) {
			throw new ServiceException("Event not found");
		}
		int rowsAffected = 0;
		try {
			rowsAffected = eventDAO.decreaseAvail(eventid, tickets);
		} catch (SQLException e) {
			throw new ServiceException(e);
		}
		
		if(rowsAffected == 0) {
			throw new ServiceException("Available tickets are not enough");
		}
	}

	/**
	 * increase the number of avail and purchase tickets of an event
	 */
	@Override
	public void increaseAvailTickets(int eventid, int tickets) {
		try {
			eventDAO.increaseAvail(eventid, tickets);
		} catch (SQLException e) {
			throw new ServiceException(e);
		}
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
