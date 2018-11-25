package cs601.project4.service;

import java.sql.SQLException;

public interface EventService {

	int createEvent(int userid, String eventName, int numTickets) throws SQLException;

	boolean purchase(int userid, int eventid, int tickets) throws SQLException;

}
