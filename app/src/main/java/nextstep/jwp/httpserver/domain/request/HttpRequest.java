package nextstep.jwp.httpserver.domain.request;

import java.util.List;
import java.util.Map;

import nextstep.jwp.httpserver.domain.Body;
import nextstep.jwp.httpserver.domain.Cookie;
import nextstep.jwp.httpserver.domain.Headers;
import nextstep.jwp.httpserver.domain.HttpSession;

public class HttpRequest {
    private final RequestLine requestLine;
    private final Headers headers;
    private final List<Cookie> cookies;
    private HttpSession session;
    private final Body body;

    public HttpRequest(RequestLine requestLine, Headers headers, List<Cookie> cookies, HttpSession session, Body body) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.cookies = cookies;
        this.session = session;
        this.body = body;
    }

    public boolean isGet() {
        return requestLine.isGet();
    }

    public boolean isPost() {
        return requestLine.isPost();
    }

    public String sessionIdInCookie() {
        return cookies.stream()
                      .filter(Cookie::isSessionId)
                      .findFirst()
                      .map(Cookie::getValue)
                      .orElse("");
    }

    public boolean hasSessionId() {
        return cookies.stream()
                .anyMatch(Cookie::isSessionId);
    }

    public String getSessionId() {
        return session.getId();
    }

    public void setSession(HttpSession session) {
        this.session = session;
    }

    public String getRequestUri() {
        return requestLine.getRequestTarget();
    }

    public HttpMethod getHttpMethod() {
        return requestLine.getHttpMethod();
    }

    public Map<String, String> getBodyToMap() {
        return body.getBody();
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public Headers getHeaders() {
        return headers;
    }

    public List<Cookie> getCookies() {
        return cookies;
    }

    public HttpSession getSession() {
        return session;
    }

    public Body getBody() {
        return body;
    }
}
