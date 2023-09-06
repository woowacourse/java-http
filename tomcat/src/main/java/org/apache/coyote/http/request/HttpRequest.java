package org.apache.coyote.http.request;

import java.util.Map;

public class HttpRequest {

    private final RequestLine requestLine;
    private final HttpHeaders headers;
    private final HttpBody httpBody;

    public HttpRequest(final RequestLine requestLine, final HttpHeaders headers, final HttpBody httpBody) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.httpBody = httpBody;
    }

    public boolean isPostRequest() {
        return requestLine.isPostMethod();
    }

    public boolean isGetRequest() {
        return requestLine.isGetMethod();
    }

    public boolean containsRequestUri(final String uri) {
        return requestLine.containsRequestUri(uri);
    }

    public boolean containsHeader(final HttpHeader headerName) {
        return headers.containsKey(headerName);
    }

    public String getHeader(final HttpHeader httpHeader) {
        return headers.get(httpHeader);
    }

    public Map<String, String> getParsedBody() {
        return httpBody.parseBodyParameters();
    }

    public RequestUri getRequestUri() {
        return requestLine.getRequestUri();
    }
}
