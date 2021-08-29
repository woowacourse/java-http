package nextstep.jwp.http.message.request;

import java.util.Optional;
import java.util.function.Supplier;

import nextstep.jwp.http.message.element.*;
import nextstep.jwp.http.message.request.request_line.HttpMethod;
import nextstep.jwp.http.message.request.request_line.HttpPath;
import nextstep.jwp.http.message.request.request_line.RequestLine;

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
        return getHeader("Cookie")
                .map(Cookie::new)
                .orElseGet(Cookie::new);
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
