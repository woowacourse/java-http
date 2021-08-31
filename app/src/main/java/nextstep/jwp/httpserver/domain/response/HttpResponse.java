package nextstep.jwp.httpserver.domain.response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nextstep.jwp.httpserver.domain.Body;
import nextstep.jwp.httpserver.domain.Cookie;
import nextstep.jwp.httpserver.domain.Headers;
import nextstep.jwp.httpserver.domain.HttpVersion;

public class HttpResponse {
    private StatusLine statusLine;
    private Headers headers;
    private Body body;
    private List<Cookie> cookies;

    public static class Builder {
        private StatusLine statusLine;
        private Headers headers;
        private Body body;
        private List<Cookie> cookies;

        public Builder() {
            headers = new Headers();
        }

        public Builder statusCode(StatusCode code) {
            this.statusLine = new StatusLine(code);
            return this;
        }

        public Builder header(String key, String value) {
            this.headers.addHeader(key, value);
            return this;
        }

        public Builder body(Map<String, String> body) {
            this.body = new Body(body);
            return this;
        }

        public Builder cookies(List<Cookie> cookies) {
            this.cookies = new ArrayList<>(cookies);
            return this;
        }

        public HttpResponse build() {
            return new HttpResponse(this);
        }
    }

    public HttpResponse() {
        this(new StatusLine(), new Headers(), new Body(), new ArrayList<>());
    }

    private HttpResponse(Builder builder) {
        this(builder.statusLine, builder.headers, builder.body, builder.cookies);
    }

    public HttpResponse(StatusLine statusLine, Headers headers, Body body, List<Cookie> cookies) {
        this.statusLine = statusLine;
        this.headers = headers;
        this.body = body;
        this.cookies = cookies;
    }

    public void ok() {
        statusLine = new StatusLine(StatusCode.OK);
    }

    public void redirect(String url) {
        this.statusLine = new StatusLine(StatusCode.FOUND);
        addHeader("Location", url);
    }

    public void addHeader(String key, String value) {
        this.headers.addHeader(key, value);
    }

    public void addCookie(Cookie cookie) {
        cookies.add(cookie);
    }

    public String statusLine() {
        final HttpVersion httpVersion = statusLine.getHttpVersion();
        final StatusCode statusCode = statusLine.getStatusCode();
        return httpVersion.getVersion() + " " + statusCode.getCode() + " " + statusCode.getStatusText() + " ";
    }

    public String responseToString(String responseBody) {
        return String.join("\r\n",
                statusLine(),
                headers.responseFormat(),
                allCookies(),
                "",
                responseBody
        );
    }

    private String allCookies() {
        final StringBuilder result = new StringBuilder();
        for (Cookie cookie : cookies) {
            result.append("Set-Cookie: ")
                  .append(generateCookieString(cookie))
                  .append("\r\n");
        }
        return result.toString();
    }

    private String generateCookieString(Cookie cookie) {
        final StringBuilder cookieHeader = new StringBuilder();
        cookieHeader.append(cookie.getName());
        cookieHeader.append("=");
        final String value = cookie.getValue();
        if (value != null && value.length() > 0) {
            cookieHeader.append(value);
        }
        return cookieHeader.toString();
    }

    public StatusLine getStatusLine() {
        return statusLine;
    }

    public Headers getHeaders() {
        return headers;
    }

    public Body getBody() {
        return body;
    }
}
