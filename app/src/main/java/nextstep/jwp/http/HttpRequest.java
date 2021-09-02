package nextstep.jwp.http;

import nextstep.jwp.http.entity.HttpBody;
import nextstep.jwp.http.entity.HttpCookie;
import nextstep.jwp.http.entity.HttpHeaders;
import nextstep.jwp.http.entity.HttpMethod;
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
        HttpCookie httpCookie = HttpCookie.of(httpHeaders.get("Cookie"));

        return new HttpRequest(requestLine.httpMethod(), requestLine.httpUri(), requestLine.httpVersion(), httpHeaders,
                httpBody, httpCookie);
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

    public boolean containsCookie(String cookieName) {
        return httpCookie.containsKey(cookieName);
    }
}
