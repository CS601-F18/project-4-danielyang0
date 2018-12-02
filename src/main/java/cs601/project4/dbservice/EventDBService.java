package cs601.project4.dbservice;

import java.sql.SQLException;
import java.util.List;

import cs601.project4.bean.Event;

public interface EventDBService {

	public int createEvent(int userid, String eventName, int numTickets) throws SQLException;

	public void purchase(int eventid, int tickets) throws SQLException;

	public List<Event> listEvents() throws SQLException;

	public Event getEvent(int id) throws SQLException;

	void increaseAvailTickets(int eventid, int tickets) throws SQLException;

	public List<Event> getMultipleEvents(List<Integer> ids) throws SQLException;

}
