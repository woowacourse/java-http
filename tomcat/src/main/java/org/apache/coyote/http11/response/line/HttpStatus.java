package org.apache.coyote.http11.response.line;

/**
 * @see <a href="https://developer.mozilla.org/ko/docs/Web/HTTP/Reference/Status">MDN HTTP 응답 상태 코드</a>
 * */

public enum HttpStatus {

    OK(200, "OK"),
    FOUND(302, "FOUND"),
    UNAUTHORIZED(401, "Unauthorized"),
    NOT_FOUND(404, "Not Found"),
    ;

    private final int code;
    private final String message;

    HttpStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}
