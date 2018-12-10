package cs601.project4.dbservice;

import java.sql.SQLException;

import org.junit.Test;

import cs601.project4.dbservice.EventDBService;
import cs601.project4.dbservice.EventDBServiceImpl;
import cs601.project4.dbservice.DBServiceProxy;

public class EventServiceTest {
	@Test
	public void testCreateEvent() {
		EventDBService es = DBServiceProxy.getProxy(EventDBService.class, new EventDBServiceImpl());
		es.createEvent(2, "outdoor", 7);
	}
	
	@Test
	public void testDecreseTicket() throws SQLException {
		EventDBServiceImpl es = new EventDBServiceImpl();
		boolean success = es.decreaseTicket(7, 8);
		System.out.println(success? "success":"fail");
	}
	
	@Test
	public void testPurchase() {
		DBServiceProxy.DEBUG_MODE = true;
		EventDBService es = DBServiceProxy.getProxy(EventDBService.class, new EventDBServiceImpl());
		es.purchase(3, 9);
	}
 	
}
