package org.apache.coyote.http11.message.response;

public enum HttpStatus {

    OK(200, "OK"),
    NOT_FOUND(404, "Not Found"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
    BAD_REQUEST(400, "Bad Request"),
    UNAUTHORIZED(401, "Unauthorized"),
    FORBIDDEN(403, "Forbidden"),
    METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
    CREATED(201, "Created"),
    ACCEPTED(202, "Accepted"),
    NO_CONTENT(204, "No Content"),
    MOVED_PERMANENTLY(301, "Moved Permanently"),
    FOUND(302, "Found"),
    SEE_OTHER(303, "See Other"),
    NOT_MODIFIED(304, "Not Modified"),
    TEMPORARY_REDIRECT(307, "Temporary Redirect"),
    PERMANENT_REDIRECT(308, "Permanent Redirect");

    private final int code;
    private final String description;

    // 생성자
    HttpStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }

    // 코드 값을 반환하는 메소드
    public int getCode() {
        return code;
    }

    // 설명을 반환하는 메소드
    public String getDescription() {
        return description;
    }
}
