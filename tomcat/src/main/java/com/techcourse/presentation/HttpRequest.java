package com.techcourse.presentation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.Headers;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpRequestParser;
import org.apache.coyote.http11.RequestLine;

public record HttpRequest(
        RequestLine requestLine,
        Headers headers,
        Map<String, String> params
) {
    public int getContentLength() {
        return headers.getContentLength();
    }

    public String getValueString(final String name) {
        return headers.getValueString(name);
    }

    public Session getSession(final boolean create) {
        final HttpCookie httpCookie = new HttpCookie(this);

        if (httpCookie.hasAttribute("JSESSIONID")) {
            final String token = httpCookie.getAttribute("JSESSIONID");
            final Session session = SessionManager.getInstance().findSession(token);
            if (session != null) {
                return session;
            }
        }

        if (create) {
            final UUID token = UUID.randomUUID();
            final Session session = new Session(token.toString());
            SessionManager.getInstance().add(session);
            return session;
        }

        return null;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getProtocol() {
        return requestLine.getProtocol();
    }

    public String getUri() {
        return requestLine.getUri();
    }

    public String getMethod() {
        return requestLine.getMethod();
    }

    public static class Builder {
        private RequestLine requestLine;
        private Headers headers = new Headers();
        private Map<String, String> params = new HashMap<>();

        public Builder requestLine(final RequestLine requestLine) {
            this.requestLine = requestLine;
            return this;
        }

        public Builder headers(final List<String> headerStrings) {
            this.headers = new Headers(headerStrings);
            return this;
        }

        public Builder params(final String body) {
            this.params = HttpRequestParser.parseParameters(body);
            return this;
        }

        public HttpRequest build() {
            return new HttpRequest(this.requestLine, this.headers, new HashMap<>(this.params));
        }
    }
}
