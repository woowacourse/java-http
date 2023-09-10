package org.apache.coyote.http11;

import java.util.Map;
import org.apache.catalina.SessionManager;

public class HttpRequest {

    private static final SessionManager sessionManager = new SessionManager();

    private final HttpRequestLine httpRequestLine;
    private final Map<String, String> headers;
    private final Map<String, String> cookies;
    private final Map<String, String> queryString;
    private final String body;

    public HttpRequest(HttpRequestLine httpRequestLine, Map<String, String> headers, Map<String, String> cookies,
                       Map<String, String> queryString, String body) {
        this.httpRequestLine = httpRequestLine;
        this.headers = headers;
        this.cookies = cookies;
        this.queryString = queryString;
        this.body = body;
    }

    public String getPath() {
        return httpRequestLine.getPath();
    }

    public Map<String, String> getQueryString() {
        return queryString;
    }

    public String getBody() {
        return body;
    }

    public HttpMethod getMethod() {
        return httpRequestLine.getMethod();
    }

    public Map<String, String> getCookie() {
        return cookies;
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }
}
