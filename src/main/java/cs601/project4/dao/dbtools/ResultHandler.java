package cs601.project4.dao.dbtools;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * interface for handle result set and extract information to Java objects
 * @author yangzun
 *
 * @param <T>
 */
public interface ResultHandler<T> {
	T handle(ResultSet rs) throws SQLException;
}
