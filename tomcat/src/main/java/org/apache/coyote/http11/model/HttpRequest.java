package org.apache.coyote.http11.model;

import org.apache.coyote.http11.session.Session;

import java.util.Map;

public class HttpRequest {

    private final HttpMethod method;
    private final String path;
    private final String version;
    private final QueryParameter queryParameter;
    private final Map<String, String> headers;
    private Session session;

    public HttpRequest(HttpMethod method,
                       String path,
                       String version,
                       QueryParameter queryParameter,
                       Map<String, String> headers) {

        this.method = method;
        this.path = path;
        this.version = version;
        this.queryParameter = queryParameter;
        this.headers = headers;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getQueryParameter(String key) {
        return queryParameter.getParameter(key);
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public String getHeader(String name) {
        return headers.get(name);
    }

    public Cookie getCookies() {
        return new Cookie(getHeader("Cookie"));
    }

    public Session getSession() {
        return session;
    }
}
