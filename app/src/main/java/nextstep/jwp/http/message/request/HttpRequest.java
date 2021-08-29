package nextstep.jwp.http.message.request;

import nextstep.jwp.http.message.element.*;
import nextstep.jwp.http.message.element.cookie.Cookie;
import nextstep.jwp.http.message.element.cookie.HttpCookie;
import nextstep.jwp.http.message.element.cookie.ProxyHttpCookie;
import nextstep.jwp.http.message.element.session.HttpSession;
import nextstep.jwp.http.message.element.session.HttpSessions;
import nextstep.jwp.http.message.request.request_line.HttpMethod;
import nextstep.jwp.http.message.request.request_line.HttpPath;
import nextstep.jwp.http.message.request.request_line.RequestLine;

import java.util.Optional;

public class HttpRequest {

    private final RequestLine requestLine;
    private final Headers headers;
    private final Body body;

    private HttpSession httpSession;
    private Cookie cookie;

    public HttpRequest(RequestLine requestLine, Headers headers, Body body) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.body = body;
    }

    public Optional<String> getHeader(String header) {
        return headers.getHeader(header);
    }

    public HttpMethod getHttpMethod() {
        return requestLine.getHttpMethod();
    }

    public HttpPath getPath() {
        return requestLine.getPath();
    }

    public HttpVersion getVersion() {
        return requestLine.getVersion();
    }

    public Body getBody() {
        return body;
    }

    public Cookie getCookie() {
        return Optional.ofNullable(cookie)
                .orElseGet(() -> {
                            ProxyHttpCookie cookie = getHeader("Cookie")
                                    .map(HttpCookie::new)
                                    .map(ProxyHttpCookie::new)
                                    .orElseGet(ProxyHttpCookie::new);

                            this.cookie = cookie;
                            return cookie;
                        }
                );
    }

    public HttpSession getSession() {
        return Optional.ofNullable(httpSession)
                .orElseGet(() -> {
                    HttpSession session = new HttpSession();
                    HttpSessions.put(session);
                    this.httpSession = session;
                    return session;
                });
    }
}
