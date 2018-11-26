package cs601.project4.exception;

public class ParamParseException extends RuntimeException {

	public ParamParseException(String msg) {
		super(msg);
	}

	public ParamParseException(Throwable t) {
		super(t);
	}

	public ParamParseException(String msg, Throwable t) {
		super(msg, t);
	}
	
}

