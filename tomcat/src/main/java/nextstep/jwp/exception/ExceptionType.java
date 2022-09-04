package nextstep.jwp.exception;

public enum ExceptionType {

	NotFoundHttpMethodException("해당 HTTP Method 는 존재하지 않습니다.");

	ExceptionType(String message) {
		this.message = message;
	}

	private final String message;

	public String getMessage() {
		return message;
	}
}
