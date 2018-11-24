package cs601.project4.service;

import java.sql.SQLException;

import org.junit.Test;

import cs601.project4.dao.dbtools.DbHelper;

public class UserServiceTest {
	@Test
	public void testCreateUser() {
		UserService us = new UserService();
		try {
			long id = us.createUser("john");
			System.out.println(id);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testGetIncreasedId() {
		long lastIncreasedID = DbHelper.getLastIncreasedID();
		System.out.println(lastIncreasedID);
	}

 	
}
