package cs601.project4.dao.dbtools;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ScalarHandler<T> implements ResultHandler<T> {

	@Override
	public T handle(ResultSet rs) throws SQLException {
		if (rs.next()) {
			return (T) rs.getObject(1);
		}
		return null;
	}

}
