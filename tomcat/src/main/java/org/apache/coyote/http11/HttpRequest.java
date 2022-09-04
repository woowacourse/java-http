package org.apache.coyote.http11;

import java.util.List;

public class HttpRequest {

    private static final String DEFAULT_CONTENT_TYPE = "text/html";

    private final RequestLine requestLine;
    private final RequestHeaders headers;
    // todo: body 추가

    private HttpRequest(final RequestLine requestLine, final RequestHeaders headers) {
        this.requestLine = requestLine;
        this.headers = headers;
    }

    public static HttpRequest of(List<String> header) {
        return new HttpRequest(
                RequestLine.of(header.get(0)),
                RequestHeaders.of(header.subList(1, header.size()))
        );
    }

    public String findContentType() {
        String accept = headers.get("Accept");
        if (accept == null) {
            return DEFAULT_CONTENT_TYPE;
        }
        return accept.split(",")[0];
    }

    public RequestUri getRequestUri() {
        return requestLine.getRequestUri();
    }
}
