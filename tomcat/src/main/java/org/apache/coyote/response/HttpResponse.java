package org.apache.coyote.response;

import org.apache.coyote.common.ContentType;
import org.apache.coyote.common.HttpStatus;
import org.apache.coyote.common.HttpVersion;

public class HttpResponse {

    private static final String DELIMITER = "\r\n";

    private final HttpResponseLine httpResponseLine;
    private final HttpResponseHeaders httpResponseHeaders;
    private final HttpResponseBody httpResponseBody;

    public HttpResponse(final HttpVersion httpVersion) {
        this.httpResponseLine = new HttpResponseLine(httpVersion);
        this.httpResponseHeaders = new HttpResponseHeaders();
        this.httpResponseBody = new HttpResponseBody();
    }

    public void setStatus(final HttpStatus httpStatus) {
        httpResponseLine.setHttpStatus(httpStatus);
    }

    public void setContentType(final String type) {
        if (type == null) {
            httpResponseHeaders.addHeader("Content-Type", ContentType.APPLICATION_OCTET_STREAM.getType());
        }
        httpResponseHeaders.addHeader("Content-Type", type);
    }

    public void setLocation(final String page) {
        httpResponseHeaders.addHeader("Location", page);
    }

    public void addCookie(final String name, final String value) {
        httpResponseHeaders.addCookie(name, value);
    }

    public void setContent(final String body) {
        httpResponseHeaders.addHeader("Content-Length", String.valueOf(body.getBytes().length));
        httpResponseBody.setBody(body);
    }

    @Override
    public String toString() {
        return String.join(
                DELIMITER,
                httpResponseLine.toString(),
                httpResponseHeaders.toString(),
                httpResponseBody.toString()
        );
    }
}
