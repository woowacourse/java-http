package org.apache.coyote.http11.model.request;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.coyote.http11.model.Header;
import org.apache.coyote.http11.model.Headers;

public class HttpRequest {

    private static final int START_LINE_INDEX = 0;

    private final RequestLine startLine;
    private final Headers headers;
    private String body = "";

    private HttpRequest(final RequestLine requestLine, final Headers headers) {
        this.startLine = requestLine;
        this.headers = headers;
    }

    public static HttpRequest from(final String requestLine, final List<String> headerLines) {
        List<Header> headers = headerLines.stream()
                .map(Header::of)
                .collect(Collectors.toList());
        return new HttpRequest(new RequestLine(requestLine), new Headers(headers));
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

    public String getHeaderValue(final String key) {
        return this.headers.getValue(key);
    }

    public void addBody(final String body) {
        this.body = body;
    }
}
