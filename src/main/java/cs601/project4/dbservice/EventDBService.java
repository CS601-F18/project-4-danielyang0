package cs601.project4.dbservice;

import java.sql.SQLException;
import java.util.List;

import cs601.project4.bean.Event;

public interface EventDBService {

	public int createEvent(int userid, String eventName, int numTickets);

	public void purchase(int eventid, int tickets);

	public List<Event> listEvents();

	public Event getEvent(int id);

	void increaseAvailTickets(int eventid, int tickets);

	public List<Event> getMultipleEvents(List<Integer> ids);

}
