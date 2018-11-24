package cs601.project4.service;

import java.sql.SQLException;

import org.junit.Test;

public class EventServiceTest {
	@Test
	public void testCreateEvent() {
		EventService es = new EventService();
		try {
			es.createEvent(2, "outdoor", 7);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testDecreseTicket() throws SQLException {
		EventService es = new EventService();
		boolean success = es.decreaseTicket(7, 8);
		System.out.println(success? "success":"fail");
	}
 	
}
