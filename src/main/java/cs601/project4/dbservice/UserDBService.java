package cs601.project4.dbservice;

import java.sql.SQLException;
import java.util.List;

import cs601.project4.bean.Ticket;
import cs601.project4.bean.User;

public interface UserDBService {
	public int createUser(String userName) throws SQLException;
//	public List<Ticket> getUserDetails(int userid) throws SQLException;
	User getUserById(int userid) throws SQLException;
}
