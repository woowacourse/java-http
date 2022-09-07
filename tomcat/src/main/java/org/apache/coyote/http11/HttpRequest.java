package org.apache.coyote.http11;

public class HttpRequest {

    private final String requestLine;
    private final HttpHeader httpHeader;
    private final HttpBody httpBody;

    public HttpRequest(final String requestLine, final HttpHeader httpHeader, final HttpBody httpBody) {
        this.requestLine = requestLine;
        this.httpHeader = httpHeader;
        this.httpBody = httpBody;
    }

    public boolean hasJSESSIONID() {
        return httpHeader.hasJSESSIONID();
    }

    public String getJSESSIONID() {
        return httpHeader.getJSESSIONID();
    }

    public String getBodyValue(final String key) {
        return httpBody.getValue(key);
    }

    public String getRequestLine() {
        return requestLine;
    }

    public String getMethod() {
        return requestLine.split(" ")[0];
    }

    public String getUrl() {
        return requestLine.split(" ")[1];
    }
}
