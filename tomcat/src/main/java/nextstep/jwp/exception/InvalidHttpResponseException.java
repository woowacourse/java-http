package nextstep.jwp.exception;

public class InvalidHttpResponseException extends RuntimeException {
	private final String message;

	public InvalidHttpResponseException(ExceptionType e) {
		this.message = e.getMessage();
	}

	@Override
	public String getMessage() {
		return message;
	}
}
