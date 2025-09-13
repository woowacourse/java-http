package com.techcourse.presentation;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpRequestParser;
import org.apache.coyote.http11.RequestLine;

public record HttpRequest(
        RequestLine requestLine,
        LinkedHashMap<String, String> headers,
        Map<String, String> params
) {
    public int getContentLength() {
        final String value = headers.get("Content-Length");
        if (value == null) {
            return 0;
        }
        return Integer.parseInt(value);
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

    public static class Builder {
        private RequestLine requestLine;
        private LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        private Map<String, String> params = new HashMap<>();

        public Builder requestLine(final RequestLine requestLine) {
            this.requestLine = requestLine;
            return this;
        }

        public Builder headers(final List<String> headerStrings) {
            final LinkedHashMap<String, String> headers = new LinkedHashMap<>();
            for (final String header : headerStrings) {
                final String[] parts = header.split(":");
                headers.put(parts[0].trim(), parts[1].trim());
            }

            this.headers = headers;
            return this;
        }

        public Builder params(final String body) {
            this.params = HttpRequestParser.parseParameters(body);
            return this;
        }

        public HttpRequest build() {
            return new HttpRequest(this.requestLine, new LinkedHashMap<>(this.headers), new HashMap<>(this.params));
        }
    }
}
