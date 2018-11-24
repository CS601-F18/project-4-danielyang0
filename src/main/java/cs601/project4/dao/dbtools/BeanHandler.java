package cs601.project4.dao.dbtools;

import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * 
 * A class for translating a result set to an single JavaBean
 * used org.apache.commons.dbutils as reference
 * 
 * @author yangzun
 *
 * @param <T>
 */
public class BeanHandler<T> implements ResultHandler<T> {

    /**
     * The Class of beans produced by this handler.
     */
    private final Class<T> type;

    /**
     * The RowProcessor to use when converting rows
     * into beans.
     */
    private final RowProcessor<T> rowProcessor = new RowProcessor<>();

    /**
     * Creates a new instance of BeanHandler.
     *
     * @param type The Class that objects returned from <code>handle()</code>
     * are created from.
     */
    public BeanHandler(Class<T> type) {
        this.type = type;
    }

    /**
     * Convert the first row of the <code>ResultSet</code> into a bean with the
     * <code>Class</code> given in the constructor.
     * @param rs <code>ResultSet</code> to process.
     * @return An initialized JavaBean or <code>null</code> if there were no
     * rows in the <code>ResultSet</code>.
     *
     * @throws SQLException if a database access error occurs
     */
    @Override
    
    public T handle(ResultSet rs) throws SQLException {
        return rs.next() ? rowProcessor.toBean(rs, this.type) : null;
    }

}
