package org.apache.coyote.http11.request;

import java.util.Map;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.common.Cookie;
import org.apache.coyote.http11.common.HttpCookies;
import org.apache.coyote.http11.common.HttpHeaders;
import org.apache.coyote.http11.common.HttpMethod;

public class HttpRequest {

    private final RequestLine requestLine;
    private final HttpHeaders headers;
    private final RequestBody requestBody;

    public HttpRequest(RequestLine requestLine, HttpHeaders headers, RequestBody requestBody) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.requestBody = requestBody;
    }

    public boolean sessionNotExists() {
        Map<String, String> headersMap = headers.getHeaders();
        String cookieString = headersMap.get(HttpHeaders.COOKIE);
        if (cookieString == null) {
            return true;
        }

        HttpCookies cookie = HttpCookies.from(cookieString);
        if (!cookie.contains(Cookie.JSESSIONID)) {
            return true;
        }

        return SessionManager.findSession(cookie.get(Cookie.JSESSIONID)) == null;
    }

    public Session getSession(boolean createIfNotExists) {
        boolean sessionNotExists = sessionNotExists();

        if (sessionNotExists && createIfNotExists) {
            Session session = new Session();
            SessionManager.add(session);
            return session;
        }

        if (sessionNotExists) {
            return null;
        }

        HttpCookies cookie = HttpCookies.from(headers.getCookie());
        String sessionId = cookie.get(Cookie.JSESSIONID);
        return SessionManager.findSession(sessionId);
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public boolean isSameMethod(HttpMethod method) {
        return requestLine.getMethod().equals(method);
    }

    public String getExtension() {
        return requestLine.getExtension();
    }

    public String getPathWithExtension() {
        return requestLine.getPathWithExtension();
    }

    public Map<String, String> getQueryString() {
        return requestLine.getQueryString();
    }

    public Map<String, String> getParams() {
        return requestBody.getParams();
    }
}
