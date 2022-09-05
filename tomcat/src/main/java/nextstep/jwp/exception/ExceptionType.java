package nextstep.jwp.exception;

public enum ExceptionType {

	NotFoundHttpMethodException("해당 HTTP Method 는 존재하지 않습니다."),
	INVALID_REQUEST_LINE_EXCEPTION("해당 REQUEST 는 유효하지 않습니다."),
	INVALID_URL_EXCEPTION("잘못된 URL 형식입니다."),
	NOT_FOUND_CONTENT_TYPE_EXCEPTION("잘못된 Content type 입니다.");

	ExceptionType(String message) {
		this.message = message;
	}

	private final String message;

	public String getMessage() {
		return message;
	}
}
