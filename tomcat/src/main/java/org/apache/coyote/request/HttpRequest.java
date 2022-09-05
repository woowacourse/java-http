package org.apache.coyote.request;

import java.util.List;
import java.util.Map;
import org.apache.coyote.exception.InvalidRequestException;

public class HttpRequest {

    public static final int REQUEST_LINE_INDEX = 0;

    private final RequestLine requestLine;
    private final HttpHeaders httpHeaders;

    public HttpRequest(final RequestLine requestLine, final HttpHeaders httpHeaders) {
        this.requestLine = requestLine;
        this.httpHeaders = httpHeaders;
    }

    public static HttpRequest from(final List<String> requests) {
        validateRequest(requests);
        return new HttpRequest(RequestLine.from(requests.get(REQUEST_LINE_INDEX)), HttpHeaders.from(requests));
    }

    private static void validateRequest(final List<String> requests) {
        if (requests.isEmpty() || requests == null) {
            throw new InvalidRequestException();
        }
    }

    public boolean isGet() {
        return requestLine.isGet();
    }

    public boolean isPost() {
        return requestLine.isPost();
    }

    public HttpMethod getHttpMethod() {
        return requestLine.getHttpMethod();
    }

    public String getRequestUri() {
        return requestLine.getRequestUri();
    }

    public Map<String, String> getQueryParams() {
        return requestLine.getQueryParams();
    }

    public Map<String, String> getHttpHeaders() {
        return httpHeaders.getValue();
    }
}
