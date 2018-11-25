package cs601.project4.dao;

import java.sql.SQLException;
import java.util.List;


import cs601.project4.bean.User;
import cs601.project4.dao.dbtools.DbHelper;

public class UserDAO {
	
	/**
	 * add a user with unique name
	 * @param user
	 * @return the number of rows affected
	 * @throws SQLException
	 */
	public int addUserWithUniqueName(User user) throws SQLException {
		String sql = "insert into t_user (name) SELECT * FROM (SELECT ?) AS tmp WHERE NOT EXISTS (SELECT name FROM t_user WHERE name = ?) LIMIT 1;";
		Object[] params = { user.getName(), user.getName()};//
		int rows = DbHelper.executeSQL(sql, params);
		return rows;
	}
	
	public List<User> getUsers() throws SQLException {
		return DbHelper.getResult("select * from t_user", User.class);
	}
	
	
	public void updateUser(User user) throws SQLException {
		String sql = "update t_user set name=? where id=?";
		DbHelper.executeSQL(sql, user.getName(),user.getId());
	}
	
	public User getUserByName(String name) throws SQLException {
		return DbHelper.getSingleResult("select * from t_user where name=?", User.class, name);
	}
	
	public User getUserById(int id) throws SQLException {
		return DbHelper.getSingleResult("select * from t_user where id=?", User.class, id);
	}
	
	public boolean isUserExsited(int userid) throws SQLException {
		String sql = "select * from t_user WHERE id=?";
		return DbHelper.getSingleResult(sql, User.class, userid) != null? true: false;
	}
}
