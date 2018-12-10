package cs601.project4.dbservice;

import java.sql.SQLException;

import org.junit.Test;

import cs601.project4.bean.User;
import cs601.project4.dao.dbtools.DbHelper;
import cs601.project4.dbservice.DBServiceProxy;
import cs601.project4.dbservice.UserDBService;
import cs601.project4.dbservice.UserDBServiceImpl;

public class UserServiceTest {
	@Test
	public void testCreateUser() {
		UserDBServiceImpl us = new UserDBServiceImpl();
		long id = us.createUser("john");
		System.out.println(id);
	}
	
	@Test
	public void testGetIncreasedId() {
		long lastIncreasedID = DbHelper.getLastIncreasedID();
		System.out.println(lastIncreasedID);
	}
	
	@Test
	public void testGetUserDetails() {
		DBServiceProxy.DEBUG_MODE = true;
		UserDBService us = DBServiceProxy.getProxy(UserDBService.class, new UserDBServiceImpl()); 
//			us.getUserDetails(1);
		User userById = us.getUserById(1);
		System.out.println(userById);
	}
	
	@Test
	public void testTransfer() {
		DBServiceProxy.DEBUG_MODE = true;
		TicketDBService us = DBServiceProxy.getProxy(TicketDBService.class, new TicketDBServiceImpl()); 
		//user 1 transfer 5 tickets of event 3 to user 2 
		us.transferTickets(1,2,3,5);
	}

 	
}
