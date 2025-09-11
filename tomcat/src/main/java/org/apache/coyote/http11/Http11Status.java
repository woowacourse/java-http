package org.apache.coyote.http11;

import java.util.Arrays;
import org.apache.coyote.http11.exception.Http11ParseException;
import org.apache.coyote.http11.exception.ParseError;

public enum Http11Status {

    OK(200, "HTTP/1.1 200 OK"),
    FOUND(302, "HTTP/1.1 302 Found"),
    BAD_REQUEST(400, "HTTP/1.1 400 Bad Request"),
    UNAUTHORIZED(401, "HTTP/1.1 401 Unauthorized"),
    NOT_FOUND(404, "HTTP/1.1 404 Not Found"),
    INTERNAL_SERVER_ERROR(500, "HTTP/1.1 500 Internal Server Error");

    private final int code;
    private final String statusLine;

    Http11Status(int code, String statusLine) {
        this.code = code;
        this.statusLine = statusLine;
    }

    public int getCode() {
        return code;
    }

    public String getStatusLine() {
        return statusLine;
    }

    public static Http11Status findByCode(int code) throws Http11ParseException {
        return Arrays.stream(values())
                .filter(status -> status.code == code)
                .findFirst()
                .orElseThrow(() -> new Http11ParseException(ParseError.INVALID_HTTP_METHOD));
    }
}

