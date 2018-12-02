package cs601.project4.dbservice;

import java.sql.SQLException;

import org.junit.Test;

import cs601.project4.dbservice.DBServiceProxy;
import cs601.project4.dbservice.UserDBService;
import cs601.project4.dbservice.UserDBServiceImpl;

public class ProxyTest {
	@Test
	public void testCreateUserWithProxy() {
		DBServiceProxy.DEBUG_MODE = true;
		try {
			UserDBService proxy = DBServiceProxy.getProxy(UserDBService.class, new UserDBServiceImpl());
			try {
				long id = proxy.createUser("somebody");
				System.out.println("created: userid:" + id);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		
	}
}
