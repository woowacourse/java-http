package nextstep.jwp.framework.common;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public enum HttpStatusCode {
    OK(200, "OK"),
    CREATED(201, "Created"),
    NO_CONTENT(204, "No Content"),
    MOVED_PERMANENTLY(301, "Moved Permanently"),
    FOUND(302, "Found"),
    BAD_REQUEST(400, "Bad Request"),
    UNAUTHORIZED(401, "Unauthorized"),
    FORBIDDEN(403, "Forbidden"),
    NOT_FOUND(404, "Not Found"),
    METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
    CONFLICT(409, "Conflict"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error");

    private static final Map<Integer, HttpStatusCode> mappings = new HashMap<>();

    static {
        for (HttpStatusCode httpStatusCode : values()) {
            mappings.put(httpStatusCode.code, httpStatusCode);
        }
    }

    private final int code;
    private final String description;

    HttpStatusCode(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public static HttpStatusCode from(int code) {
        return mappings.computeIfAbsent(
                code,
                key -> {
                    throw new NoSuchElementException(String.format("서버에서 지원하는 HttpStatusCode 가 아닙니다.(%s)", key));
                }
        );
    }

    public String getCodeAsString() {
        return String.valueOf(code);
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
