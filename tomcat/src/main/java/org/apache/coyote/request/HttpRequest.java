package org.apache.coyote.request;

import java.util.List;
import java.util.Map;
import org.apache.coyote.exception.InvalidRequestException;

public class HttpRequest {

    private static final int REQUEST_LINE_INDEX = 0;

    private final RequestLine requestLine;
    private final HttpHeaders httpHeaders;
    private final RequestBody requestBody;

    public HttpRequest(final RequestLine requestLine, final HttpHeaders httpHeaders, final RequestBody requestBody) {
        this.requestLine = requestLine;
        this.httpHeaders = httpHeaders;
        this.requestBody = requestBody;
    }

    public static HttpRequest from(final List<String> requests, final String requestBody) {
        validateRequest(requests);
        return new HttpRequest(RequestLine.from(requests.get(REQUEST_LINE_INDEX)), HttpHeaders.from(requests),
                RequestBody.from(requestBody));
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

    public Map<String, String> getRequestBody() {
        return requestBody.getValue();
    }
}
