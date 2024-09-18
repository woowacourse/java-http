package org.apache.coyote.http11.message.request;

import java.net.URI;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpCookies;
import org.apache.coyote.http11.message.common.ContentType;
import org.apache.coyote.http11.message.common.HttpHeaders;
import org.apache.coyote.session.Session;
import org.apache.coyote.session.SessionManager;

public class HttpRequest {

    private final HttpRequestLine requestLine;
    private final HttpHeaders headers;
    private final HttpRequestBody requestBody;

    public HttpRequest(HttpRequestLine requestLine, HttpHeaders headers, HttpRequestBody requestBody) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.requestBody = requestBody;
    }

    public String getBodyParameter(String key) {
        return requestBody.getBodyParameter(key);
    }

    public ContentType getContentType() {
        return headers.getContentType();
    }

    public URI getUri() {
        return requestLine.getUri();
    }

    public boolean hasPath(String path) {
        return requestLine.hasPath(path);
    }

    public HttpCookies getCookies() {
        return headers.getCookies();
    }

    public HttpMethod getMethod() {
        return requestLine.getMethod();
    }

    public Session getSession() {
        HttpCookies cookies = getCookies();
        HttpCookie cookie = cookies.getCookie(Session.JSESSIONID);
        Session session = SessionManager.getInstance().findSession(cookie.getValue());

        if (session == null) {
            return new Session();
        }
        return session;
    }
}
