package org.apache.coyote.http11.request;

import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;
import javax.annotation.Nullable;
import org.apache.catalina.cookie.Cookie;
import org.apache.catalina.session.Session;

public class HttpRequest {

    private final HttpRequestStartLine startLine;
    private final HttpRequestHeaders headers;
    private final HttpRequestBody body;

    public HttpRequest(HttpRequestStartLine startLine, HttpRequestHeaders headers, HttpRequestBody body) {
        this.startLine = startLine;
        this.headers = headers;
        this.body = body;
    }

    public HttpMethod getMethod() {
        return startLine.getMethod();
    }

    public String getPath() {
        return startLine.getPath();
    }

    public Optional<String> getParameter(String key) {
        if (HttpMethod.POST == getMethod() && "application/x-www-form-urlencoded".equals(headers.getContentType())) {
            return body.getParameter(key);
        }
        return startLine.getQueryParameter(key);
    }

    public boolean matchHeader(String key, String expectedValue) {
        return headers.getHeader(key)
                .map(value -> value.equals(expectedValue))
                .orElse(false);
    }

    @Nullable
    public String getContentType() {
        return headers.getContentType();
    }

    public Cookie getCookie() {
        return headers.getCookie();
    }

    public Optional<Session> getSession() {
        return getCookie().getSession();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        HttpRequest request = (HttpRequest) object;
        return Objects.equals(startLine, request.startLine)
                && Objects.equals(headers, request.headers)
                && Objects.equals(body, request.body);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startLine, headers, body);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", HttpRequest.class.getSimpleName() + "[", "]")
                .add("startLine=" + startLine)
                .add("headers=" + headers)
                .add("body='" + body + "'")
                .toString();
    }
}
