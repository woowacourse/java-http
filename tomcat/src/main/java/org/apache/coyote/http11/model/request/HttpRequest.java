package org.apache.coyote.http11.model.request;

import java.util.List;
import java.util.Map;
import org.apache.coyote.http11.model.Header;
import org.apache.coyote.http11.model.Headers;

public class HttpRequest {

    private final RequestLine startLine;
    private final Headers headers;
    private String body = "";

    private HttpRequest(final RequestLine requestLine, final Headers headers) {
        this.startLine = requestLine;
        this.headers = headers;
    }

    public static HttpRequest from(final String requestLine, final List<String> headerLines) {
        return new HttpRequest(new RequestLine(requestLine), Headers.of(headerLines));
    }

    public Method getMethod() {
        return startLine.getMethod();
    }

    public String getUrl() {
        return startLine.getUrl();
    }

    public Map<String, String> getQueryParams() {
        return startLine.getQueryParams();
    }

    public String getHeaderValue(final Header header) {
        return this.headers.getValue(header.getKey());
    }

    public void addBody(final String body) {
        this.body = body;
    }
}
