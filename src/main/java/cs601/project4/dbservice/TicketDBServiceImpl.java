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
	 * get tickets which belongs to a user
	 * 
	 */
	@Override
	public List<Ticket> getUserTickets(int userid) {
		List<Ticket> tickets = null;
		try {
			tickets = ticketDAO.getTicketByUserId(userid);
		} catch (SQLException e) {
			throw new ServiceException(e);
		}
		return tickets;
	}
	
	/**
	 * if user exist, increase tickets number for a user, otherwise, do nothing
	 */
	@Override
	public boolean addTicketsToUserIfExists(int userid, int eventid, int ticketsQuantity) {
		try {
			User user = userDAO.getUserById(userid);
			if(user == null) {
				return false;
			}
			return ticketDAO.increaseTicketsForUser(userid, eventid, ticketsQuantity);
		} catch (SQLException e) {
			throw new ServiceException(e);
		}
	}
	
	
	/**
	 * 
	 * transfer ticket from user to target user
	 */
	@Override
	public boolean transferTickets(int userid, int targetUserid, int eventid, int ticketsQuantity) {
		User user = null;
		try {
			user = userDAO.getUserById(userid);
		} catch (SQLException e) {
		}
		if(user == null) {
			return false;
		}
		User targetUser = null;
		try {
			targetUser = userDAO.getUserById(targetUserid);
		} catch (SQLException e) {
		}
		if(targetUser == null) {
			return false;
		}
		boolean decreaseSuccess = false;
		try {
			decreaseSuccess = ticketDAO.decreaseTicketsFromUser(userid, eventid, ticketsQuantity);
		} catch (SQLException e) {
		}
		if(!decreaseSuccess) {
			throw new ServiceException("user tickets quantity is not enough");
		}
		try {
			return ticketDAO.increaseTicketsForUser(targetUserid, eventid, ticketsQuantity);
		} catch (SQLException e) {
			throw new ServiceException(e);
		}
	}
	
}
