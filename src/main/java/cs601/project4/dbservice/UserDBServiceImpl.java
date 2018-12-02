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
	 * POST /create {"username": "string"} -> {"userid": 0} | User unsuccessfully created
	 * create a user given a username if the username is unique in database
	 * @param username
	 * @return the new created user's id or 0 if the name is not unique
	 * @throws SQLException
	 */
	public int createUser(String username) throws SQLException {
		User user = new User();
		user.setName(username);
		return userDAO.addUserWithUniqueName(user) == 0? 0: DbHelper.getLastIncreasedID();
	}
	
	@Override
	public User getUserById(int userid) throws SQLException {
		User user = userDAO.getUserById(userid);
		return user;
	}
	
//	/**
//	 * GET /{userid} () -> {"userid": 0,"username": "string","tickets": [{"eventid": 0}]} || User not found
//	 * 
//	 */
//	@Override
//	public List<Ticket> getUserDetails(int userid) throws SQLException {
//		User user = userDAO.getUserById(userid);
//		if(user == null) {
//			throw new ServiceException("User not found");
//		}
//		List<Ticket> tickets = ticketDAO.getTicketByUserId(userid);
////		for (Ticket ticket : tickets) {
////			System.out.println("eventid:"+ticket.getEventid()+", ticketsQuantity:"+ticket.getQuantity());
////		}
//		return tickets;
//	}
	
//	@Override
//	public void purchaseTicketsForUser(int userid, int eventid, int ticketsQuantity) {
//		//TO DO
//	}
	
	
	
}
