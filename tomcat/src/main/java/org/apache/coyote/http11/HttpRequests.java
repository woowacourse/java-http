package org.apache.coyote.http11;

public class HttpRequests {

    private final String method;
    private final String httpRequest;
    private final String protocol;
    private final HttpHeader httpHeader;
    private final HttpBody httpBody;
    private final HttpCookies httpCookies;
    private final Session session;

    public HttpRequests(
            HttpInfo httpInfo,
            HttpHeader httpHeader,
            HttpBody httpBody,
            HttpCookies httpCookies,
            Session session
    ) {
        this.method = httpInfo.getMethod();
        this.httpRequest = httpInfo.getPath();
        this.protocol = httpInfo.getProtocol();
        this.httpHeader = httpHeader;
        this.httpBody = httpBody;
        this.httpCookies = httpCookies;
        this.session = session;
    }

    public String getMethod() {
        return method;
    }

    public String getHttpRequest() {
        return httpRequest;
    }

    public String getProtocol() {
        return protocol;
    }

    public HttpHeader getHttpHeader() {
        return httpHeader;
    }

    public HttpBody getHttpBody() {
        return httpBody;
    }

    public HttpCookies getHttpCookies() {
        return httpCookies;
    }

    public Session getSession() {
        return session;
    }
}
