package org.apache.coyote.error;

import org.apache.coyote.httpResponse.StatusCode;

public enum ErrorCode {

    NOT_ALLOW_PATH("지원하지 않은 경로입니다.", StatusCode.NOT_FOUND),
    NOT_ALLOW_METHOD("지원하지 않은 HTTP 메서드입니다.", StatusCode.NOT_ALLOW_METHOD),
    NOT_EXISTS_STATIC_RESOURCE("존재하지 않은 정적 파일입니다.", StatusCode.NOT_FOUND),
    NOT_EXISTS_MEMBER("존재하지 않은 회원이거나, 비밀번호가 잘못 되었습니다.", StatusCode.UNAUTHORIZED),
    NOT_ALLOW_MEDIA_TYPE("지원하지 않는 Content-Type 입니다.", StatusCode.NOT_SUPPORTED_MEDIA_TYPE);

    private final String message;
    private final StatusCode statusCode;

    ErrorCode(
            final String message,
            final StatusCode statusCode
    ) {
        this.message = message;
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }
}
