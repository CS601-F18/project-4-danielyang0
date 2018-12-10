package cs601.project4.dao;

import java.sql.SQLException;
import java.util.List;

import cs601.project4.bean.Event;
import cs601.project4.dao.dbtools.DbHelper;

/**
 * Date access object for Event class
 * @author yangzun
 *
 */
public class EventDAO {
	
	/**
	 * add a new event into the database
	 * @param event
	 * @return
	 * @throws SQLException
	 */
	public int addEvent(Event event) throws SQLException {
		String sql = "insert into t_event(name,userid,avail,purchased) values(?,?,?,?)";
		Object[] params = { event.getName() , event.getUserid(), event.getAvail(), event.getPurchased()};
		return DbHelper.executeSQL(sql, params);
	}
	
	
	/**
	 * get all the events from DB
	 * @return
	 * @throws SQLException
	 */
	public List<Event> getEvents() throws SQLException {
		return DbHelper.getResult("select * from t_event", Event.class);
	}
	
	/**
	 * decrease available and purchased tickets of an event if and only if 
	 * the available tickets after decresement is still larger or equal than 0
	 * @param eventid
	 * @param tickets
	 * @return
	 * @throws SQLException
	 */
	public int decreaseAvail(int eventid, int tickets) throws SQLException {
		String sql = "update t_event set avail=avail-?, purchased=purchased+? WHERE id=? and avail>=?";
		Object[] params = { tickets, tickets, eventid, tickets };
		return DbHelper.executeSQL(sql, params);
	}
	
	/**
	 * increase avail and purchased
	 * @param eventid
	 * @param tickets
	 * @return
	 * @throws SQLException
	 */
	public int increaseAvail(int eventid, int tickets) throws SQLException {
		String sql = "update t_event set avail=avail+?, purchased=purchased-? WHERE id=?";
		Object[] params = { tickets, tickets, eventid };
		return DbHelper.executeSQL(sql, params);
	}
	

	/**
	 * get the event according to its id
	 * @param id
	 * @return
	 * @throws SQLException
	 */
	public Event getEventById(int id) throws SQLException {
		return DbHelper.getSingleResult("select * from t_event where id=?", Event.class, id);
	}
}
