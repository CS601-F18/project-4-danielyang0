package cs601.project4.dao.dbtools;

import java.sql.Connection;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * A class for executing sql statements including query and update
 * @author yangzun
 *
 */
public class SqlExecuter {
	private static Logger logger = Logger.getLogger(SqlExecuter.class);
	static {
		PropertyConfigurator.configure("./config/log4j.properties");
	}
	/**
	 * execute a query sql statement, get the result to either a JavaBean or a list of JavaBean
	 * according to the handler parameter
	 * @param conn
	 * @param sql
	 * @param handler
	 * @param params
	 * @return
	 * @throws SQLException
	 */
	public <T> T query(Connection conn, String sql, ResultHandler<T> handler, Object... params)
			throws SQLException {
		if (conn == null) {
			throw new SQLException("Null connection");
		}

		if (sql == null) {
			throw new SQLException("Null SQL statement");
		}

		if (handler == null) {
			throw new SQLException("Null ResultSetHandler");
		}
		PreparedStatement stmt = null;
		ResultSet rs = null;
		T result = null;
		try {
			stmt = conn.prepareStatement(sql);
			setParamsIntoStmt(stmt, params);
			rs = stmt.executeQuery();
			result = handler.handle(rs);
		} catch (SQLException e) {
			throw e;
		} finally {
			try {
				if(rs!=null) rs.close();
			} finally {
				if(stmt != null) stmt.close();
			}
		}
		return result;
	}

	/**
	 * execute a update sql statement, return the rows affected
	 * @param conn
	 * @param sql
	 * @param params
	 * @return
	 * @throws SQLException
	 */
    public int update(Connection conn, String sql, Object... params) throws SQLException {
        if (conn == null) {
            throw new SQLException("Null connection");
        }

        if (sql == null) {
            throw new SQLException("Null SQL statement");
        }

        PreparedStatement stmt = null;
        int rows = 0;

        try {
            stmt = conn.prepareStatement(sql);
            setParamsIntoStmt(stmt, params);
            rows = stmt.executeUpdate();
        } catch (SQLException e) {
            throw e;
        } finally {
        	if(stmt != null) stmt.close();
        }
        return rows;
    }
	
	
    /**
     * @param stmt
     * @param params
     * @throws SQLException
     */
	private void setParamsIntoStmt(PreparedStatement stmt, Object... params) 
			throws SQLException {
		ParameterMetaData pmd = stmt.getParameterMetaData();
		int stmtCount = pmd.getParameterCount();
		int paramsCount = params == null ? 0 : params.length;

		if (stmtCount != paramsCount) {
			throw new SQLException("Wrong number of parameters: expected "
					+ stmtCount + ", was given " + paramsCount);
		}
		if (params == null) {
			return;
		}
		for (int i = 0; i < params.length; i++) {
			if (params[i] != null) {
				stmt.setObject(i + 1, params[i]);
			} else {
				int sqlType = Types.VARCHAR;
				try {
					sqlType = pmd.getParameterType(i + 1);
				} catch (SQLException e) {
					throw e;
				}
				stmt.setNull(i + 1, sqlType);
			}
		}

	}
	
}
