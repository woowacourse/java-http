package nextstep.jwp.exception;

public enum ExceptionType {

	INVALID_HTTP_METHOD_EXCEPTION("잘못된 HTTP Method 형식입니다."),
	INVALID_REQUEST_LINE_EXCEPTION("잘못된 REQUEST 형식입니다."),
	INVALID_URL_EXCEPTION("잘못된 URL 형식입니다."),
	INVALID_STATUS_CODE_EXCEPTION("잘못된 StatusCode 형식입니다."),
	INVALID_HTTP_VERSION_EXCEPTION("잘못된 HTTP Version 형식입니다."),
	INVALID_HTTP_HEADER_TYPE_EXCEPTION("잘못된 HttpHeaderType 형식입니다."),
	INVALID_CONTENT_TYPE_EXCEPTION("잘못된 Content type 형식입니다.");

	ExceptionType(String message) {
		this.message = message;
	}

	private final String message;

	public String getMessage() {
		return message;
	}
}
