package org.apache.coyote.http;

import java.util.Map;

public class HttpRequest {

    private final StartLine startLine;
    private final HttpHeaders headers;
    private final HttpBody httpBody;

    public HttpRequest(final StartLine startLine, final HttpHeaders headers, final HttpBody httpBody) {
        this.startLine = startLine;
        this.headers = headers;
        this.httpBody = httpBody;
    }

    public boolean isPostRequest() {
        return startLine.isPostMethod();
    }

    public boolean isGetRequest() {
        return startLine.isGetMethod();
    }

    public boolean containsRequestUri(final String uri) {
        return startLine.containsRequestUri(uri);
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
        return startLine.getRequestUri();
    }
}
