package cs601.project4.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.junit.Test;

import cs601.project4.bean.User;
import cs601.project4.dao.dbtools.DbHelper;

public class UserDAOTest {

	@Test
	public void testThreadLocal() {
		Connection connection1 = DbHelper.getConnection();
		Connection connection2 = DbHelper.getConnection();
		System.out.println(connection1 == connection2);
	}

	@Test
	public void testGetAllUsers() {
		UserDAO userDAO = new UserDAO();
		List<User> users = null;
		try {
			users = userDAO.getUsers();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		users.forEach(e -> {System.out.println(e.getId()+":"+e.getName());});
	}
	
	@Test
	public void testGetUserById() {
		UserDAO userDAO = new UserDAO();
		User user = null;
		try {
			user = userDAO.getUserById(777);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println(user.getId()+":"+user.getName());
	}
	
	
	@Test
	public void testGetUserByName() {
		UserDAO userDAO = new UserDAO();
		User user = null;
		try {
			user = userDAO.getUserByName("woaini");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if(user == null) {
			System.out.println("null");
		}else{
			System.out.println(user.getId()+user.getName());
		}
	}
	
	@Test
	public void testAddUser() {
		UserDAO userDAO = new UserDAO();
		User user = new User();
		user.setName("zhangsan");
		try {
			userDAO.addUserWithUniqueName(user);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testNotCommit() {
		Connection connection = DbHelper.getConnection();
		try {
			connection.setAutoCommit(false);
			UserDAO userDAO = new UserDAO();
			User user = new User();
			user.setName("john");
			userDAO.addUserWithUniqueName(user);
			userDAO.getUsers().forEach((e) -> {System.out.println(e.getName());});
			//			connection.commit();
		} catch (Exception e) {
			try {
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		} finally {
			try {
				DbHelper.closeConnection(connection);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}


	class MyThread extends Thread {
		Connection[] cs;
		int idx;
		public MyThread(Connection[] cs, int idx) {
			this.cs = cs;
			this.idx = idx;
		}
		@Override
		public void run() {
			cs[idx] = DbHelper.getConnection();
		}
	}

	@Test
	public void testTwoConnectionOperatingAtTheSameTime() {
		Connection c1 = DbHelper.getConnection();
		Connection c2 = null;
		Connection[] cs = new Connection[2];
		cs[0] = c1;
		cs[1] = c2;
		Thread r = new MyThread(cs,0);
		Thread r2 = new MyThread(cs,1);
		r.start();
		r2.start();
		try {
			r.join();
			//			r2.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(cs[0] == cs[1]);
		System.out.println(c1 == cs[0]);

	}

	@Test
	public void testTransactionOnDifferenctConnections () {
		Thread t1 = new Thread() {
			public void run() {
				UserDAO userDAO = new UserDAO();
				Connection c = DbHelper.getConnection();
				try {
					c.setAutoCommit(false);
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				User user = null;
				try {
					user = userDAO.getUserByName("john");
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				System.out.println("thread1: john exited? " + (user != null));

				if(user == null) {
					User john = new User();john.setName("john");
					try {
						userDAO.addUserWithUniqueName(john);
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
					System.out.println("thread1: john added");
					try {
						System.out.println("thread1: john exited? " + (userDAO.getUserByName("john") != null));
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
					try {
						System.out.println("thread1 sleep 3 seconds");
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					try {
						c.commit();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		};
		t1.start();

		Thread t2 = new Thread() {
			public void run() {
				try {
					System.out.println("thread2 sleep 2 seconds");
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				UserDAO userDAO = new UserDAO();
				Connection c = DbHelper.getConnection();
				User user = null;
				try {
					user = userDAO.getUserByName("john");
				} catch (SQLException e2) {
					e2.printStackTrace();
				}
				System.out.println("thread2: john exited? " + (user != null));

				try {
					c.setAutoCommit(false);
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				if(user == null) {
					User john = new User();john.setName("john");
					try {
						userDAO.addUserWithUniqueName(john);
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
					System.out.println("thread2: john added");
					try {
						System.out.println("thread2: john exited? " + (userDAO.getUserByName("john") != null));
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					try {
						c.commit();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
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
	
	private Connection[] cs = new Connection[2];
	
	
	@Test
	public void testTransactionOnDifferenctConnections2 () {
		Thread t1 = new Thread() {
			public void run() {
				UserDAO userDAO = new UserDAO();
				Connection c = DbHelper.getConnection();
				cs[0] = c;
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
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				user.setName(user.getName()+"n");
				try {
					userDAO.updateUser(user);
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					Thread.sleep(5000);
					System.out.println("thread1 wake up after 5 seconds");
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				try {
					System.out.println(user.getName());
					System.out.println(userDAO.getUserById(1).getName());
					System.out.println("=====thread 1 commit");
					cs[0].commit();
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
				System.out.println(c);
				try {
					User user = userDAO.getUserByName("yangzun");
				} catch (SQLException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				
				try {
					c.setAutoCommit(false);
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				try {
					System.out.println("=====thread 2 commit");
					cs[0].commit();
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

//	@Test
//	public void testTransactionOnSameConnection () {
//		Connection commonConnection = DbHelper.getConnection();
//		try {
//			commonConnection.setAutoCommit(false);
//		} catch (SQLException e2) {
//			e2.printStackTrace();
//		}
//		Thread t1 = new Thread() {
//			public void run() {
//				DbHelper.setConnection(commonConnection);
//				UserDAO userDAO = new UserDAO();
//				User user = userDAO.getUserByName("john");
//				System.out.println("thread1: john exited? " + (user != null));
//				if(user == null) {
//					User john = new User();john.setName("john");
//					userDAO.addUser(john);
//					System.out.println("thread1: john added");
//					System.out.println("thread1: john exited? " + (userDAO.getUserByName("john") != null));
//					try {
//						commonConnection.commit();
//					} catch (SQLException e) {
//						e.printStackTrace();
//					}
//				}
//			}
//		};
//		t1.start();
//
//		Thread t2 = new Thread() {
//			public void run() {
//				DbHelper.setConnection(commonConnection);
//				UserDAO userDAO = new UserDAO();
//				try {
//					System.out.println("thread2 sleep 3 seconds");
//					Thread.sleep(3000);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//				User user = userDAO.getUserByName("john");
//				System.out.println("thread2: john exited? " + (user != null));
//
//				if(user == null) {
//					User john = new User();john.setName("john");
//					userDAO.addUser(john);
//					System.out.println("thread2: john added");
//					System.out.println("thread2: john exited? " + (userDAO.getUserByName("john") != null));
//				}
//
//			}
//		};
//		t2.start();
//		try {
//			t1.join();
//			t2.join();
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		try {
//			commonConnection.commit();
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//	}






}
