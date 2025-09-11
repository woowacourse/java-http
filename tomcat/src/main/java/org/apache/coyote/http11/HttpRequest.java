package org.apache.coyote.http11;

import java.util.Map;

public class HttpRequest {

    private final String resourcePath;
    private final QueryParameter queryParameter;
    private final Map<String, String> headers;
    private Session session;

    public HttpRequest(String resourcePath, QueryParameter queryParameter, Map<String, String> headers) {
        this.resourcePath = resourcePath;
        this.queryParameter = queryParameter;
        this.headers = headers;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public String getQueryParameter(String key) {
        return queryParameter.getParameter(key);
    }

    public boolean hasQueryParameter() {
        return queryParameter.hasAnyParameter();
    }

    public String getHeader(String name) {
        return headers.get(name);
    }

    public Cookie getCookies() {
        return new Cookie(getHeader("Cookie"));
    }
}
