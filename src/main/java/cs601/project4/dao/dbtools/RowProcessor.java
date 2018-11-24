package cs601.project4.dao.dbtools;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;


/**
 * A class for processing a row in the result set
 * @author yangzun
 *
 * @param <T>
 */
public class RowProcessor<T> {
	private static Logger logger = Logger.getLogger(RowProcessor.class);
	private static final Class<ResultSet> RESULTSET_CLASS = ResultSet.class;
	/**
	 * map the name of basic java type to the corresponding method name in the resultSet
	 * for example: "String" is mapped to the String getString(String columnLabel) method in ResultSet class
	 */
	private static Map<String, Method> resultSetMethodMap;
	static {
		PropertyConfigurator.configure("./config/log4j.properties");
		resultSetMethodMap = new HashMap<>();
		try {
			resultSetMethodMap.put("String", RESULTSET_CLASS.getDeclaredMethod("getString", String.class));
			resultSetMethodMap.put("int", RESULTSET_CLASS.getDeclaredMethod("getInt", String.class));
		} catch (NoSuchMethodException | SecurityException e) {
		}
	}
	
	/**
	 * translate member name in a class to its setter method name
	 * for example: "ticket" is transalted to "setTicket"
	 * @param name
	 * @return
	 */
	private static String setterName(String name) {
		String substring = name.substring(1);
		String upperCase = name.substring(0, 1).toUpperCase();
		return "set" + upperCase + substring;
	}
	
	/**
	 * used Reflection to extract information in the result set, 
	 * and set them into the JavaBean
	 * @param rs
	 * @param beanClass
	 * @return
	 */
	public T toBean(ResultSet rs, Class<T> beanClass) {
		T bean = null;
		try {
			bean = beanClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
			return bean;
		}
		Field[] beanFields = beanClass.getDeclaredFields();
		for (Field beanField : beanFields) {
			String memberTypeName = beanField.getType().getSimpleName();
			String memberName = beanField.getName();
			Method rsMethod = resultSetMethodMap.get(memberTypeName);
			try {
				Object resultFromResultSet = rsMethod.invoke(rs, memberName);
				Method setterMethod = null;
				try {
					setterMethod = beanClass.getDeclaredMethod(setterName(memberName), beanField.getType());
				} catch (NoSuchMethodException | SecurityException e) {
					e.printStackTrace();
					return null;
				}
				setterMethod.invoke(bean, resultFromResultSet);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		return bean;
	}
	
	/**
	 * used Reflection to extract information in the result set, 
	 * and set them into the List of JavaBean
	 * @param rs
	 * @param beanClass
	 * @return
	 * @throws SQLException
	 */
    public List<T> toBeanList(ResultSet rs, Class<T> beanClass) throws SQLException {
        List<T> results = new ArrayList<T>();
        while (rs.next()) {
        	T bean = toBean(rs, beanClass);
            if(bean != null) {
            	results.add(bean);
            }
        }
        return results;
    }


}
