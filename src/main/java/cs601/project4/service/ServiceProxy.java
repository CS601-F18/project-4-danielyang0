package cs601.project4.service;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.util.Scanner;

import cs601.project4.dao.dbtools.DbHelper;
import cs601.project4.exception.ServiceException;

//http://www.importnew.com/27772.html
/**
 * JDK dynamic proxy used for creating a proxy object for service class.
 * so that every service method will be put into a transaction
 * @author yangzun
 *
 */
public class ServiceProxy {
	/**
	 * DEBUG_MODE should only be set true when doing testing
	 */
	public static boolean DEBUG_MODE = false;
	
	/**
	 * get the proxy object for service object
	 * @param c
	 * @param service
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static <T> T getProxy(Class<T> c, Object service) throws IllegalArgumentException, SecurityException, ClassCastException {
		Object newProxyInstance = Proxy.newProxyInstance(
				ServiceProxy.class.getClassLoader(), // 1. class loader
			    new Class<?>[] {c}, // 2. the interfaces which needs to be implemeted
			    new TxInvocationHandler(service));// 3. the real handler for a method call 
		return c.cast(newProxyInstance);
	}
}

class TxInvocationHandler implements InvocationHandler{
    private Object service;
    public TxInvocationHandler(Object service) {
        this.service = service;
    }
    /**
     * every service method will be put into a transaction,
     * the isolation level is default. (When using mysql, it is REPEATABLE READ)
     * and after execution, the database connection will be closed.
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    	Object result = null;
    	Connection conn = DbHelper.getConnection();
    	try {
    		conn.setAutoCommit(false);
    		result = method.invoke(service, args);
    		//only used when testing transaction
    		if(ServiceProxy.DEBUG_MODE) {
    			Scanner sc = new Scanner(System.in);
    			System.out.println("PRESS 'y' TO COMMIT: " + method.getName() + "\nPRESS OTHER KEYs TO ROLLBACK");
    			String input = sc.nextLine();
    			if(!"y".equals(input)) {
    				throw new Exception();
    			}
    		}
    		conn.commit();
		} catch (Exception e) {
			conn.rollback();
			throw new ServiceException(e);
		}finally{
			DbHelper.closeConnection(conn);
		}
    	return result;
    }
}
