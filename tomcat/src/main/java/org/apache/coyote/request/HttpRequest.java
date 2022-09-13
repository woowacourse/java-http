package org.apache.coyote.request;

import java.util.List;
import java.util.Map;
import org.apache.coyote.cookie.Cookie;
import org.apache.coyote.cookie.Cookies;
import org.apache.coyote.exception.InvalidRequestException;
import org.apache.coyote.session.Session;
import org.apache.coyote.session.SessionManager;

public class HttpRequest {

    private static final String SESSION = "JSESSIONID";

    private final RequestLine requestLine;
    private final HttpHeaders httpHeaders;
    private final RequestBody requestBody;
    private Session session;

    public HttpRequest(final RequestLine requestLine, final HttpHeaders httpHeaders, final RequestBody requestBody) {
        this.requestLine = requestLine;
        this.httpHeaders = httpHeaders;
        this.requestBody = requestBody;
    }

    public static HttpRequest of(final String requestLine, final List<String> requests, final String requestBody) {
        validateRequest(requestLine);
        return new HttpRequest(RequestLine.from(requestLine), HttpHeaders.from(requests),
                RequestBody.from(requestBody));
    }

    private static void validateRequest(final String requestLine) {
        if (requestLine == null) {
            throw new InvalidRequestException();
        }
    }

    public boolean isGet() {
        return requestLine.isGet();
    }

    public boolean isPost() {
        return requestLine.isPost();
    }

    public HttpMethod getHttpMethod() {
        return requestLine.getHttpMethod();
    }

    public String getRequestUri() {
        return requestLine.getRequestUri();
    }

    public Map<String, String> getQueryParams() {
        return requestLine.getQueryParams();
    }

    public Map<String, String> getHttpHeaders() {
        return httpHeaders.getValue();
    }

    public RequestBody getRequestBody() {
        return requestBody;
    }

    public Session getSession() {
        Cookies cookies = httpHeaders.getCookies();
        Cookie sessionCookie = cookies.find(SESSION);
        if (sessionCookie == null) {
            return Session.init();
        }
        return SessionManager.findSession(sessionCookie.getValue());
    }
}
