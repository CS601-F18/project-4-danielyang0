package cs601.project4.service;

import java.sql.SQLException;

import org.junit.Test;

public class ProxyTest {
	@Test
	public void testCreateUserWithProxy() {
		ServiceProxy.DEBUG_MODE = true;
		try {
			UserService proxy = ServiceProxy.getProxy(UserService.class, new UserServiceImpl());
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
