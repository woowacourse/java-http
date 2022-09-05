package nextstep.jwp.exception;

public class InvalidHttpRequestException extends RuntimeException {
	private final String message;

	public InvalidHttpRequestException(ExceptionType e) {
		this.message = e.getMessage();
	}

	@Override
	public String getMessage() {
		return message;
	}
}
