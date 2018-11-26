package cs601.project4.service;

import java.sql.SQLException;

import org.junit.Test;

import cs601.project4.service.EventServiceImpl;
import cs601.project4.service.ServiceProxy;

public class EventServiceTest {
	@Test
	public void testCreateEvent() {
		EventService es = ServiceProxy.getProxy(EventService.class, new EventServiceImpl());
		try {
			es.createEvent(2, "outdoor", 7);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testDecreseTicket() throws SQLException {
		EventServiceImpl es = new EventServiceImpl();
		boolean success = es.decreaseTicket(7, 8);
		System.out.println(success? "success":"fail");
	}
	
	@Test
	public void testPurchase() {
		ServiceProxy.DEBUG_MODE = true;
		EventService es = ServiceProxy.getProxy(EventService.class, new EventServiceImpl());
		try {
			es.purchase(1, 3, 9);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
 	
}
