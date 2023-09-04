package nextstep.jwp.response;

import java.util.Map;
import nextstep.jwp.common.HttpStatus;
import nextstep.jwp.common.HttpVersion;

public class ResponseEntity {
    private final HttpVersion httpVersion;
    private final HttpStatus httpStatus;
    private final String content;
    private final Map<String, String> headers;

    private ResponseEntity(HttpVersion httpVersion, HttpStatus httpStatus, String content, Map<String, String> headers) {
        this.httpVersion = httpVersion;
        this.httpStatus = httpStatus;
        this.content = content;
        this.headers = headers;
    }

    public static ResponseEntity of(final HttpVersion httpVersion, final HttpStatus httpStatus, final String content,
                                    final Map<String, String> headers) {
        return new ResponseEntity(httpVersion, httpStatus, content, headers);
    }

    public HttpVersion getHttpVersion() {
        return httpVersion;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getContent() {
        return content;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}
