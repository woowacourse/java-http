package org.apache.coyote.http11.request;

import org.apache.coyote.http11.cookie.Cookies;
import org.apache.coyote.http11.path.Path;
import org.apache.coyote.http11.header.Headers;
import org.apache.coyote.http11.method.HttpMethod;
import org.apache.coyote.http11.queryparam.QueryParams;
import org.apache.coyote.http11.session.Session;

public class HttpRequest {
    private final HttpMethod method;
    private final Path path;
    private final QueryParams queryParams;
    private final String version;
    private final Headers headers;
    private final RequestBody body;
    private final Cookies cookies;
    private Session session;

    public HttpRequest(final RequestLine requestLine, final Headers headers, final RequestBody body, final Cookies cookies) {
        this.method = requestLine.getHttpMethod();
        this.path = requestLine.getPath();
        this.queryParams = requestLine.getQueryParams();
        this.version = requestLine.getVersion();
        this.headers = headers;
        this.body = body;
        this.cookies = cookies;
    }

    public Headers getHeaders() {
        return headers;
    }

    public String getPath() {
        return path.value();
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getVersion() {
        return version;
    }

    public QueryParams getQueryParams() {
        return queryParams;
    }

    public String getQueryParam(final String name) {
        return queryParams.getQueryParam(name);
    }

    public RequestBody getBodies() {
        return body;
    }

    public String getBodyAttribute(final String name) {
        return body.getAttribute(name);
    }

    public Cookies getCookies() {
        return cookies;
    }

    public String getCookie(final String name) {
        return cookies.getCookie(name);
    }

    public void setSession(final Session session) {
        this.session = session;
    }

    public Session getSession() {
        return session;
    }

    public Object getSessionAttribute(final String name) {
        return this.session.getAttribute(name);
    }

    public boolean existSessionAttribute(final String name) {
        return this.session.getAttribute(name) != null;
    }
}
