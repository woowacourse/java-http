package org.apache.coyote.http11.response;

import org.apache.coyote.http11.common.HttpStatus;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestLine;

public class HttpResponseHeader {

    private final HttpStatus httpStatus;

    private HttpResponseHeader(final HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public static HttpResponseHeader from(final HttpRequest httpRequest) {
        final RequestLine requestLine = httpRequest.requestLine();
        final var uri = requestLine.getUri();
        final var queryString = requestLine.getRequestBody();

        if (uri.startsWith("/index") && !queryString.isEmpty()) {
            return new HttpResponseHeader(HttpStatus.FOUND);
        }

        if (uri.startsWith("/401")) {
            return new HttpResponseHeader(HttpStatus.FOUND);
        }

        if (uri.startsWith("/login") && !httpRequest.cookie().noneJSessionId()) {
            return new HttpResponseHeader(HttpStatus.FOUND);
        }

        return new HttpResponseHeader(HttpStatus.OK);
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
