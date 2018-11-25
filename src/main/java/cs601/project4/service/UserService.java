package cs601.project4.service;

import java.sql.SQLException;

public interface UserService {
	public int createUser(String userName) throws SQLException;
	public void getUserDetails(int userid) throws SQLException;
	boolean transferTickets(int userid, int targetUserid, int eventid, int ticketsQuantity) throws SQLException;
}
