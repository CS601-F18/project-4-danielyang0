package cs601.project4.service;

import java.sql.SQLException;

import cs601.project4.bean.User;
import cs601.project4.dao.UserDAO;
import cs601.project4.dao.dbtools.DbHelper;

public class UserService {
	private UserDAO userDAO = new UserDAO();
	
	/**
	 * create a user given a username if the username is unique in database
	 * @param userName
	 * @return
	 * @throws SQLException
	 */
	public long createUser(String userName) throws SQLException {
		User user = new User();
		user.setName(userName);
		return userDAO.addUserWithUniqueName(user) == 0? 0: DbHelper.getLastIncreasedID();
	}
}
