package cs601.project4.dao;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.Test;

import cs601.project4.bean.User;
import cs601.project4.dao.dbtools.DbHelper;

public class TransactionTest {
	@Test
	public void test1 () {
		Thread t1 = new Thread() {
			public void run() {
				UserDAO userDAO = new UserDAO();
				Connection c = DbHelper.getConnection();
				System.out.println(c);
				try {
					c.setAutoCommit(false);
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				User user = null;
				try {
					user = userDAO.getUserByName("yangzun");
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				user.setName(user.getName()+"n");
				try {
					userDAO.updateUser(user);
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				try {
					Thread.sleep(6000);
					c.rollback();
//					System.out.println("再度:"+userDAO.getUserById(1).getName());
					user.setName("hiahia");
					userDAO.updateUser(user);
					System.out.println("再度:"+userDAO.getUserById(1).getName());
					Thread.sleep(4000);
					System.out.println("thread1 wake up after 20 seconds");
				} catch (InterruptedException | SQLException e) {
					e.printStackTrace();
				}
				try {
//					System.out.println(user.getName());
//					System.out.println(userDAO.getUserById(1).getName());
					System.out.println("=====thread 1 commit");
					c.commit();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				System.out.println("thread1 exit");
			}
		};
		t1.start();

		Thread t2 = new Thread() {
			public void run() {
				try {
					Thread.sleep(2000);
					System.out.println("thread2 start after 2 seconds");
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				UserDAO userDAO = new UserDAO();
				Connection c = DbHelper.getConnection();
				try {
					c.setAutoCommit(false);
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				
				System.out.println(c);
				User user = null;
				try {
					user = userDAO.getUserByName("yangzun");
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				user.setName(user.getName()+"x");
				try {
					userDAO.updateUser(user);
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				try {
					System.out.println("=====thread 2 commit");
					c.commit();
					System.out.println("1::"+userDAO.getUserById(1).getName());
				} catch (SQLException e) {
					e.printStackTrace();
				}

			}
		};
		t2.start();
		try {
			t1.join();
			t2.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@Test
	public void test2 () {
		Thread t1 = new Thread() {
			public void run() {
				UserDAO userDAO = new UserDAO();
				Connection c = DbHelper.getConnection();
				System.out.println(c);
				try {
					c.setAutoCommit(false);
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				User user = null;
				try {
					user = userDAO.getUserByName("yangzun");
				} catch (SQLException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				user.setName(user.getName()+"modify1");
				try {
					Thread.sleep(2000);
					System.out.println("thread1 start after 2 seconds");
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				//thread1 执行update比thread2晚
				System.out.println("thread1 update 数据开始");
				try {
					userDAO.updateUser(user);
				} catch (SQLException e2) {
					e2.printStackTrace();
				}//执行完update之后,下面thread1先尝试commit
				System.out.println("thread1 update 数据完成");
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				try {
					System.out.println("=====thread 1 commit开始");
					c.commit();//会阻塞到thread2 commit完
					System.out.println("=====thread 1 commit完成");
				} catch (SQLException e) {
					e.printStackTrace();
				}
				System.out.println("thread1 exit");
			}
		};
		t1.start();

		Thread t2 = new Thread() {
			public void run() {
				UserDAO userDAO = new UserDAO();
				Connection c = DbHelper.getConnection();
				try {
					c.setAutoCommit(false);
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				
				System.out.println(c);
				User user = null;
				try {
					user = userDAO.getUserByName("yangzun");
				} catch (SQLException e2) {
					e2.printStackTrace();
				}
				user.setName(user.getName()+"modify2");
				System.out.println("thread2 update 数据开始");
				try {
					userDAO.updateUser(user);
				} catch (SQLException e2) {
					e2.printStackTrace();
				}//thread2执行update语句较早
				System.out.println("thread2 update 数据完成");
				try {
					Thread.sleep(6000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				//等待thread1 也执行了update语句
				
				try {
					System.out.println("=====thread 2 commit开始");
					c.commit();
					System.out.println("=====thread 2 commit完成");
					System.out.println("thread 2率先完成commit之后::"+userDAO.getUserById(1).getName());
				} catch (SQLException e) {
					e.printStackTrace();
				}

			}
		};
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
