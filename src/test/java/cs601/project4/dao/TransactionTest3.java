package cs601.project4.dao;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.Test;

import cs601.project4.bean.Event;
import cs601.project4.bean.User;
import cs601.project4.dao.dbtools.DbHelper;

public class TransactionTest3 {

	public void simplesleep(String title, long millis){
		try {
			System.out.println(title + "sleep "+ millis/1000.0 +" seconds start");
			Thread.sleep(millis);
			System.out.println(title + "sleep "+ millis/1000.0 +" seconds end");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
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
			public void run() {
				EventDAO eventDAO = new EventDAO();
				Connection c = setNotAntoCommit();
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
//				System.out.println("thread 1 get data start");
//				Event event = eventDAO.getEventById(1);
//				System.out.println("thread 1 get data end");
//				
				System.out.println("thread1 update 数据开始");
				try {
					eventDAO.decreaseAvail(1,1);
				} catch (SQLException e) {
					e.printStackTrace();
				}
				System.out.println("thread1 update 数据完成");
				
				simplesleep("thread1",4000);
				
				System.out.println("thread1再次读 开始");
				try {
					System.out.println("thread1再次: "+eventDAO.getEventById(1).getAvail());
				} catch (SQLException e) {
					e.printStackTrace();
				}
				System.out.println("thread1再次读 结束");
				
				commitlog("thread1", c);
				
				System.out.println("thread1第三次读 开始");
				try {
					System.out.println("thread1第三次: "+eventDAO.getEventById(1).getAvail());
				} catch (SQLException e) {
					e.printStackTrace();
				}
				System.out.println("thread1第三次读 结束");
				System.out.println("thread1 exit");
			}
		};


		Thread t2 = new Thread() {
			public void run() {
				EventDAO eventDAO = new EventDAO();
				Connection c = setNotAntoCommit();
				System.out.println("thread 2 get data start");
				try {
					Event event = eventDAO.getEventById(1);
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				System.out.println("thread 2 get data end");
				
				simplesleep("thread2",2000);
				
				System.out.println("thread2 update 数据开始");
				try {
					System.out.println(eventDAO.getEventById(1).getAvail()+"-----------------");
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				try {
					eventDAO.decreaseAvail(1, 1);
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				System.out.println("thread2 update 数据完成");
				
				System.out.println("thread2再次读 开始");
				try {
					System.out.println("thread2再次: "+eventDAO.getEventById(1).getAvail());
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				System.out.println("thread2再次读 结束");
				
				try {
					Thread.sleep(3);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				simplesleep("thread2", 4000);
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
