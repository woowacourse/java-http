package org.apache.coyote.http11;

public class HttpRequest {

    private final HttpHeader httpHeader;
    private final HttpBody httpBody;

    public HttpRequest(final HttpHeader httpHeader, final HttpBody httpBody) {
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

    public String getMethod() {
        return httpHeader.getMethod();
    }

    public String getUrl() {
        return httpHeader.getUrl();
    }
}
