package cs601.project4.service;

import java.sql.SQLException;
import java.util.List;

import cs601.project4.bean.Event;

public interface EventService {

	int createEvent(int userid, String eventName, int numTickets) throws SQLException;

	boolean purchase(int userid, int eventid, int tickets) throws SQLException;

	List<Event> listEvents() throws SQLException;

	Event getEvent(int id) throws SQLException;

}
