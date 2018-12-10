package cs601.project4.dbservice;

import cs601.project4.bean.User;

public interface UserDBService {
	public int createUser(String userName);
	User getUserById(int userid);
}
