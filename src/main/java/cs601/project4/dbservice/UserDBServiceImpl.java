package cs601.project4.dbservice;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import cs601.project4.bean.Ticket;
import cs601.project4.bean.User;
import cs601.project4.dao.TicketDAO;
import cs601.project4.dao.UserDAO;
import cs601.project4.dao.dbtools.DbHelper;
import cs601.project4.exception.ServiceException;

public class UserDBServiceImpl implements UserDBService{
	private UserDAO userDAO = new UserDAO();
	private TicketDAO ticketDAO = new TicketDAO();
	
	/**
	 * create a user given a username if the username is unique in database
	 * @param username
	 * @return the new created user's id or 0 if the name is not unique
	 * @throws SQLException
	 */
	public int createUser(String username) {
		User user = new User();
		user.setName(username);
		int userid = 0;
		try {
			userid = userDAO.addUserWithUniqueName(user);
		} catch (SQLException e) {
			throw new ServiceException(e);
		}
		return userid == 0? 0: DbHelper.getLastIncreasedID();
	}
	
	@Override
	public User getUserById(int userid) {
		User user = null;
		try {
			user = userDAO.getUserById(userid);
		} catch (SQLException e) {
			throw new ServiceException(e);
		}
		return user;
	}
	
}
