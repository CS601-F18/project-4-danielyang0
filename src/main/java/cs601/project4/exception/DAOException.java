package cs601.project4.exception;

public class DAOException extends RuntimeException {
	public DAOException() {
	}

	public DAOException(String msg) {
		super(msg);
	}

	public DAOException(Throwable t) {
		super(t);
	}

	public DAOException(String msg, Throwable t) {
		super(msg, t);
	}
}
