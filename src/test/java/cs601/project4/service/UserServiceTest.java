package cs601.project4.service;

import java.sql.SQLException;

import org.junit.Test;

import cs601.project4.dao.dbtools.DbHelper;
import cs601.project4.service.ServiceProxy;
import cs601.project4.service.UserService;
import cs601.project4.service.UserServiceImpl;

public class UserServiceTest {
	@Test
	public void testCreateUser() {
		UserServiceImpl us = new UserServiceImpl();
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
	
	@Test
	public void testGetUserDetails() {
		ServiceProxy.DEBUG_MODE = true;
		UserService us = ServiceProxy.getProxy(UserService.class, new UserServiceImpl()); 
		try {
			us.getUserDetails(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testTransfer() {
		ServiceProxy.DEBUG_MODE = true;
		UserService us = ServiceProxy.getProxy(UserService.class, new UserServiceImpl()); 
		//user 1 transfer 5 tickets of event 3 to user 2 
		try {
			us.transferTickets(1,2,3,5);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

 	
}
