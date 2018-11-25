package cs601.project4.dao;

import java.sql.SQLException;

import org.junit.Test;

import cs601.project4.service.ServiceProxy;

public class TicketDAOTest {
	@Test
	public void testAddTicket() {
		TicketDAO ticketDAO = new TicketDAO(); 
		try {
			ticketDAO.addTicket(1, 3, 12);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testInitTicketsForUserAndLock() {
		TicketDAO td = new TicketDAO();
		try {
			td.initTicketsForUserAndLock(7, 7);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
