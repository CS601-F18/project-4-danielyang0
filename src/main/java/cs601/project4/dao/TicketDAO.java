package cs601.project4.dao;

import java.math.BigInteger;
import java.sql.SQLException;
import java.util.List;

import cs601.project4.bean.Event;
import cs601.project4.bean.Ticket;
import cs601.project4.dao.dbtools.DbHelper;

public class TicketDAO {
	/**
	 * add a new tickt to DB
	 * @param userid
	 * @param eventid
	 * @param quantity
	 * @return
	 * @throws SQLException
	 */
	public int addTicket(int userid, int eventid, int quantity) throws SQLException {
		String sql = "insert into t_ticket(userid,eventid,quantity) values(?,?,?)";
		Object[] params = { userid, eventid, quantity};
		return DbHelper.executeSQL(sql, params);
	}
	
	/**
	 * get all the tickets that belongs to a user
	 * @param userid
	 * @return
	 * @throws SQLException
	 */
	public List<Ticket> getTicketByUserId(int userid) throws SQLException {
		String sql = "select * from t_ticket WHERE userid=?";
		Object[] params = { userid };
		return DbHelper.getResult(sql, Ticket.class, params);
	}
	
	
	/**
	 * decrease the tickets which belong to a user
	 * @param userid
	 * @param eventid
	 * @param ticketsQuantity
	 * @return
	 * @throws SQLException
	 */
	public boolean decreaseTicketsFromUser(int userid, int eventid, int ticketsQuantity) throws SQLException {
		String sql = "update t_ticket set quantity=quantity-? WHERE userid=? AND eventid=? AND quantity >=?";
		Object[] params = { ticketsQuantity, userid, eventid, ticketsQuantity };
		int rowsAffected = DbHelper.executeSQL(sql, params);
		return rowsAffected != 0;
		//update t_ticket set quantity=quantity-1 WHERE userid=2 AND eventid=2 AND quantity >=1
	}
	
	
	/**
	 * add some tickets to a user
	 * @param userid
	 * @param eventid
	 * @param ticketsQuantity
	 * @return
	 * @throws SQLException
	 */
	public boolean increaseTicketsForUser(int userid, int eventid, int ticketsQuantity ) throws SQLException {
		int ticketID = initTicketsForUserAndLock(userid, eventid);
		String sql = "UPDATE t_ticket SET quantity=quantity+? WHERE id=?";
		Object[] params = { ticketsQuantity, ticketID };
		return DbHelper.executeSQL(sql, params) != 0;
	}
	
	/**
	 * add 0 tickets of an event for a user if the record does not exist
	 * lock the record
	 * @param userid
	 * @param eventid
	 * @return the ticket id
	 * @throws SQLException
	 */
	int initTicketsForUserAndLock(int userid, int eventid ) throws SQLException {
		String sql = "SELECT * FROM t_ticket WHERE userid = ? AND eventid= ? for update";
		Ticket ticket = DbHelper.getSingleResult(sql, Ticket.class, new Object[]{userid, eventid});
		if(ticket == null) {
			String insertSql = "INSERT INTO t_ticket (userid,eventid,quantity) SELECT ?,?,0";
			Object[] params = { userid, eventid };
			DbHelper.executeSQL(insertSql, params);
			return DbHelper.getLastIncreasedID();
		}else{
			return ticket.getId();
		}
	}
	
}
