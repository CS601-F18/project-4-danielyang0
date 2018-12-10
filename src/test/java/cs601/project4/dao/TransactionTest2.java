package cs601.project4.dao;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.Test;

import cs601.project4.bean.User;
import cs601.project4.dao.dbtools.DbHelper;

public class TransactionTest2 {

	public void simplesleep(String title, long millis){
		try {
//			System.out.println(title + "sleep "+ millis/1000.0 +" seconds start");
			Thread.sleep(millis);
//			System.out.println(title + "sleep "+ millis/1000.0 +" seconds end");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	private int unit = 1000;
	

	
	public Connection setNotAntoCommit() {
		Connection c = DbHelper.getConnection();
		System.out.println(c);
		try {
			c.setAutoCommit(false);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		return c;
	}
	
	public void commitlog(String title, Connection c) {
		try {
			System.out.println("===== " + title +" commit开始");
			c.commit();
			System.out.println("=====" + title + " commit完成");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void test1 () {
		Thread t1 = new Thread() {
			private String title = "thread1";
			private int curr = 0;
			public void startAtTime(int i) {
				if(i<=curr) return;
				simplesleep(title, (i-curr)*unit);
				curr = i;
			}
			public void run() {
				UserDAO userDAO = new UserDAO();
				Connection c = setNotAntoCommit();
//				try {
//					c.setTransactionIsolation(Connection. );
//				} catch (SQLException e1) {
//					e1.printStackTrace();
//				}
				startAtTime(0);
				System.out.println(title + " read data start");
				User user = null;
				try {
					user = userDAO.getUserById(1);
				} catch (SQLException e) {
					e.printStackTrace();
				}
				System.out.println(title+" read data end:" + user.getName());//xxxxn
				
				startAtTime(2);
				System.out.println(title+"再次读 开始");
				try {
					System.out.println(title+"再次读结束: "+userDAO.getUserById(1).getName());
				} catch (SQLException e) {
					e.printStackTrace();
				}//xxxxn
				
				user.setName(user.getName()+"m1");
				System.out.println(title + " update 数据开始");
				try {
					userDAO.updateUser(user);
				} catch (SQLException e) {
					e.printStackTrace();
				}
				System.out.println(title + " update 数据完成");
				
				
				commitlog("thread1", c);
				
				System.out.println("thread1 exit");
			}
		};


		Thread t2 = new Thread() {
			private String title = "thread2";
			private int curr = 0;
			public void startAtTime(int i) {
				if(i<=curr) return;
				simplesleep(title, (i-curr)*unit);
				curr = i;
			}
			public void run() {
				UserDAO userDAO = new UserDAO();
				Connection c = setNotAntoCommit();
//				try {
//					c.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ );
//				} catch (SQLException e1) {
//					e1.printStackTrace();
//				}
				
				startAtTime(1);
				System.out.println(title + " read data start");
				User user = null;
				try {
					user = userDAO.getUserById(1);
				} catch (SQLException e) {
					e.printStackTrace();
				}
				System.out.println(title+" read data end:" + user.getName());//xxxxn
				
				user.setName(user.getName()+"m2");
				
				System.out.println(title + " update 数据开始");
				try {
					userDAO.updateUser(user);
				} catch (SQLException e) {
					e.printStackTrace();
				}
				System.out.println(title + " update 数据完成");
				
				startAtTime(3);
				System.out.println(title+"再次读 开始");
				try {
					System.out.println(title+"再次读结束: "+userDAO.getUserById(1).getName());
				} catch (SQLException e) {
					e.printStackTrace();
				}//xxxxnm2
				
//				startAtTime(5);
				commitlog("thread2", c);

			}
		};
		t1.start();
		t2.start();
		try {
			t1.join();
			t2.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
}
