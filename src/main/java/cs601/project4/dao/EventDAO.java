package cs601.project4.dao;

import java.sql.SQLException;
import java.util.List;

import cs601.project4.bean.Event;
import cs601.project4.bean.User;
import cs601.project4.dao.dbtools.DbHelper;

public class EventDAO {
	
	public int addEvent(Event event) throws SQLException {
		String sql = "insert into t_event(name,userid,avail,purchased) values(?,?,?,?)";
		Object[] params = { event.getName() , event.getUserid(), event.getAvail(), event.getPurchased()};
		return DbHelper.executeSQL(sql, params);
	}
	
	public List<Event> getEvents() throws SQLException {
		return DbHelper.getResult("select * from t_event", Event.class);
	}
	
	/**
	 * decrease available tickets of an event if and only if 
	 * the available tickets after decresement is still larger or equal than 0
	 * @param eventid
	 * @param tickets
	 * @return
	 * @throws SQLException
	 */
	public int decreaseAvail(int eventid, int tickets) throws SQLException {
		String sql = "update t_event set avail=avail-? WHERE id=? and avail>=?";
		Object[] params = { tickets, eventid, tickets };
		return DbHelper.executeSQL(sql, params);
	}
	
	public int increaseAvail(int eventid, int tickets) throws SQLException {
		String sql = "update t_event set avail=avail+? WHERE id=?";
		Object[] params = { tickets, eventid };
		return DbHelper.executeSQL(sql, params);
	}
	
	
	public void updateEvent(Event event) throws SQLException {
		String sql = "update t_event set avail=? where id=?";
		DbHelper.executeSQL(sql, event.getAvail(), event.getId());
	}
	
	public Event getEventByName(String name) throws SQLException {
		return DbHelper.getSingleResult("select * from t_event where name=?", Event.class, name);
	}
	
	public Event getEventById(int id) throws SQLException {
		return DbHelper.getSingleResult("select * from t_event where id=?", Event.class, id);
	}
}
