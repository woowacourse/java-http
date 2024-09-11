package org.apache.coyote.component;

public enum HttpStatusCode {

    OK(200, "OK"),
    CREATED(201, "Created"),
    NO_CONTENT(204, "No Content"),

    FOUND(302, "Found"),

    BAD_REQUEST(400, "Bad Request"),
    UNAUTHORIZED(401, "Unauthorized"),
    FORBIDDEN(403, "Forbidden"),
    NOT_FOUND(404, "Not Found"),
    METHOD_NOT_ALLOWED(405, "Method Not Allowed"),

    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
    BAD_GATEWAY(502, "Bad Gateway"),
    ;

    private final int code;
    private final String message;

    HttpStatusCode(final int code, final String message) {
        this.code = code;
        this.message = message;
    }

    public String toHttpResponse() {
        return code + " " + message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "HttpStatusCode{" +
               "code=" + code +
               ", message='" + message + '\'' +
               '}';
    }
}
