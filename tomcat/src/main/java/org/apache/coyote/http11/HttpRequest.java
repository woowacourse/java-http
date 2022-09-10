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

    public boolean matchMethod(final String method) {
        final String requestLineMethod = requestLine.split(" ")[0];
        return requestLineMethod.equals(method);
    }

    public boolean matchUrl(final String url) {
        return getUrl().equals(url);
    }

    public String getUrl() {
        return requestLine.split(" ")[1];
    }

    public String getJSESSIONID() {
        return httpHeader.getJSESSIONID();
    }

    public String getBodyValue(final String key) {
        return httpBody.getValue(key);
    }
}
