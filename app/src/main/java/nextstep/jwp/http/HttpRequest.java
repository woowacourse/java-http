package nextstep.jwp.http;

import java.util.Objects;
import nextstep.jwp.http.entity.HttpBody;
import nextstep.jwp.http.entity.HttpCookie;
import nextstep.jwp.http.entity.HttpHeaders;
import nextstep.jwp.http.entity.HttpMethod;
import nextstep.jwp.http.entity.HttpSession;
import nextstep.jwp.http.entity.HttpUri;
import nextstep.jwp.http.entity.HttpVersion;
import nextstep.jwp.http.entity.RequestLine;

public class HttpRequest {
    private final HttpMethod method;
    private final HttpUri uri;
    private final HttpVersion httpVersion;
    private final HttpHeaders headers;
    private final HttpBody httpBody;
    private final HttpCookie httpCookie;

    public HttpRequest(HttpMethod method, HttpUri uri, HttpVersion httpVersion,
                       HttpHeaders headers, HttpBody httpBody, HttpCookie httpCookie) {
        this.method = method;
        this.uri = uri;
        this.httpVersion = httpVersion;
        this.headers = headers;
        this.httpBody = httpBody;
        this.httpCookie = httpCookie;
    }

    public static HttpRequest of(RequestLine requestLine, HttpHeaders httpHeaders, HttpBody httpBody) {
        HttpCookie httpCookie = HttpCookie.of(httpHeaders.getCookie());

        return new HttpRequest(requestLine.httpMethod(), requestLine.httpUri(), requestLine.httpVersion(), httpHeaders,
                httpBody, httpCookie);
    }

    public HttpSession getSession() {
        String sessionId = this.httpCookie.getSessionId();
        if (Objects.isNull(sessionId)) {
            return HttpSessions.createSession();
        }
        return HttpSessions.getSession(sessionId);
    }

    public HttpMethod method() {
        return method;
    }

    public HttpUri uri() {
        return uri;
    }

    public HttpVersion httpVersion() {
        return httpVersion;
    }

    public HttpHeaders headers() {
        return headers;
    }

    public HttpBody body() {
        return httpBody;
    }

    public HttpCookie httpCookie() {
        return httpCookie;
    }
}
