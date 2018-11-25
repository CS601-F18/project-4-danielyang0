package cs601.project4.exception;

public class ServiceException extends RuntimeException {
	public ServiceException() {
	}

	public ServiceException(String msg) {
		super(msg);
	}

	public ServiceException(Throwable t) {
		super(t);
	}

	public ServiceException(String msg, Throwable t) {
		super(msg, t);
	}
	
}

