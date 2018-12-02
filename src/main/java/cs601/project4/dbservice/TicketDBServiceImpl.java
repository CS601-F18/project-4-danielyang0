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

public class TicketDBServiceImpl implements TicketDBService{
	private TicketDAO ticketDAO = new TicketDAO();
	private UserDAO userDAO = new UserDAO();
	
	/**
	 * GET /{userid} () -> {"userid": 0,"username": "string","tickets": [{"eventid": 0}]} || User not found
	 * 
	 */
	@Override
	public List<Ticket> getUserTickets(int userid) throws SQLException {
		List<Ticket> tickets = ticketDAO.getTicketByUserId(userid);
//		for (Ticket ticket : tickets) {
//			System.out.println("eventid:"+ticket.getEventid()+", ticketsQuantity:"+ticket.getQuantity());
//		}
		return tickets;
//		List<Integer> ticketIds = new ArrayList<>();
//		tickets.forEach((e) -> {ticketIds.add(e.getEventid());});
//		return ticketIds;
	}
	
	@Override
	public boolean addTicketsToUserIfExists(int userid, int eventid, int ticketsQuantity) throws SQLException {
		User user = userDAO.getUserById(userid);
		if(user == null) {
			return false;
		}
		return ticketDAO.increaseTicketsForUser(userid, eventid, ticketsQuantity);
	}
	
	
	/**
	 * POST /{userid}/tickets/transfer  {"eventid": 0,"tickets": 0,"targetuser": 0} -> 
	 * Event tickets transfered || Tickets could not be transfered
	 * 
	 * transfer ticket from user to target user
	 */
	@Override
	public boolean transferTickets(int userid, int targetUserid, int eventid, int ticketsQuantity) throws SQLException {
		User user = userDAO.getUserById(userid);
		if(user == null) {
			return false;
//			throw new ServiceException("User who transfer tickets is not found");
		}
		User targetUser = userDAO.getUserById(targetUserid);
		if(targetUser == null) {
			return false;
//			throw new ServiceException("User who accept tickets is not found");
		}
		boolean decreaseSuccess = ticketDAO.decreaseTicketsFromUser(userid, eventid, ticketsQuantity);
		if(!decreaseSuccess) {
//			return false;
			throw new ServiceException("user tickets quantity is not enough");
		}
		return ticketDAO.increaseTicketsForUser(targetUserid, eventid, ticketsQuantity);
	}
	
}
