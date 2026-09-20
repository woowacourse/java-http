package org.apache.coyote.http11.request;

import java.util.Set;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;

public class HttpRequest {
    private static final String FORM_URLENCODED = "application/x-www-form-urlencoded";

    private final RequestLine requestLine;
    private final HttpHeaders headers;
    private final HttpBody body;
    private final HttpParams bodyParams;
    private final HttpCookie cookie;
    private final SessionManager sessionManager;
    private Session session;

    public HttpRequest(RequestLine requestLine, HttpHeaders headers, HttpBody body, SessionManager sessionManager) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.body = body;
        this.bodyParams = parseBodyParams(requestLine, headers, body);
        this.cookie = headers.getCookie();
        this.sessionManager = sessionManager;
    }

    public static HttpRequest from(
            String requestLine,
            HttpHeaders headers,
            HttpBody body,
            SessionManager sessionManager
    ) {
        return new HttpRequest(RequestLine.from(requestLine), headers, body, sessionManager);
    }

    public static HttpRequest from(
            String requestLine,
            HttpHeaders headers,
            HttpBody body,
            Set<HttpMethod> supportedMethods,
            SessionManager sessionManager
    ) {
        RequestLine parsedRequestLine = RequestLine.from(requestLine, supportedMethods);

        return new HttpRequest(parsedRequestLine, headers, body, sessionManager);
    }

    public HttpCookie getCookie() {
        return cookie;
    }

    public Session getSession(boolean create) {
        if (session != null) {
            return session;
        }

        session = sessionManager.findSession(cookie.get(HttpCookie.JSESSIONID));
        if (session == null && create) {
            session = sessionManager.createSession();
        }

        return session;
    }

    public Session renewSession() {
        Session previousSession = getSession(false);
        if (previousSession != null) {
            sessionManager.remove(previousSession);
        }

        session = sessionManager.createSession();

        return session;
    }

    public HttpMethod getMethod() {
        return requestLine.getMethod();
    }

    public String getHttpPath() {
        return requestLine.getPath();
    }

    public String getQueryParams(String key) {
        return requestLine.getParams(key);
    }

    public String getBodyParams(String key) {
        return bodyParams.get(key);
    }

    public String getVersion() {
        return requestLine.getVersion();
    }

    public String getHeader(String name) {
        return headers.get(name);
    }

    public String getHost() {
        String authority = requestLine.getAuthority();
        if (authority != null) {
            return authority;
        }
        return headers.get("Host");
    }

    public HttpBody getBody() {
        return body;
    }

    private static HttpParams parseBodyParams(RequestLine requestLine, HttpHeaders headers, HttpBody body) {
        if (requestLine.getMethod() != HttpMethod.POST) {
            return HttpParams.from(null);
        }

        if (!FORM_URLENCODED.equals(headers.getMediaType())) {
            return HttpParams.from(null);
        }

        return HttpParams.from(body.getContent());
    }
}
