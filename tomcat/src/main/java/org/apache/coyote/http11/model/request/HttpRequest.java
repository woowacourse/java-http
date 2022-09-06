package org.apache.coyote.http11.model.request;

import java.util.List;
import org.apache.coyote.http11.model.Header;
import org.apache.coyote.http11.model.Headers;
import org.apache.coyote.http11.model.HttpCookie;

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

    public String getHeaderValue(final Header header) {
        return this.headers.getValue(header.getKey());
    }

    public HttpCookie getCookie() {
        return this.headers.getCookie();
    }

    public void addBody(final String body) {
        this.body = body;
    }

    public String getBody() {
        return body;
    }
}
