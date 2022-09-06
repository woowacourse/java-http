package nextstep.jwp.exception;

public class InvalidHttpMethodException extends RuntimeException {
	private final String message;

	public InvalidHttpMethodException(ExceptionType e) {
		this.message = e.getMessage();
	}

	@Override
	public String getMessage() {
		return message;
	}
}
