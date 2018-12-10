package cs601.project4.dbservice;

import java.sql.SQLException;
import java.util.List;

import cs601.project4.bean.Ticket;

public interface TicketDBService {
	public List<Ticket> getUserTickets(int userid);

	boolean addTicketsToUserIfExists(int userid, int eventid, int ticketsQuantity);

	boolean transferTickets(int userid, int targetUserid, int eventid, int ticketsQuantity);

}
