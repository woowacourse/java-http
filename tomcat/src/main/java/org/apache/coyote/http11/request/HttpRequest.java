package org.apache.coyote.http11.request;

import static org.apache.coyote.http11.support.QueryStringParser.parseQueryString;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.apache.coyote.http11.web.Cookie;
import org.apache.coyote.http11.web.Session;
import org.apache.coyote.http11.web.SessionManager;

public class HttpRequest {

    private final RequestLine requestLine;
    private final HttpHeaders headers;
    private final Map<String, String> body;

    private HttpRequest(final RequestLine requestLine, final HttpHeaders headers,
                        final Map<String, String> body) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.body = body;
    }

    public static HttpRequest from(final String startLine, final List<String> headerLines, final BufferedReader reader)
            throws IOException {
        final RequestLine requestLine = RequestLine.of(startLine);
        final HttpHeaders httpHeaders = HttpHeaders.of(headerLines);

        final Map<String, String> body = new HashMap<>(Collections.EMPTY_MAP);
        if (httpHeaders.hasContentLength()) {
            final int contentLength = Integer.parseInt(httpHeaders.getContentLength());
            final char[] buffer = new char[contentLength];
            reader.read(buffer, 0, contentLength);
            final String requestBody = new String(buffer);
            body.putAll(parseQueryString(requestBody));
        }

        return new HttpRequest(requestLine, httpHeaders, body);
    }

    public boolean isGet() {
        return requestLine.isGet();
    }

    public String getUrl() {
        return requestLine.getUrl();
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public Map<String, String> getHeaders() {
        return headers.getValues();
    }

    public Map<String, String> getBody() {
        return body;
    }

    public Session getSession() {
        if (!hasCookie()) {
            return createSession();
        }
        final Cookie cookie = getCookie();
        final Optional<Session> session = SessionManager.findSession(cookie.getValue());
        return session.orElseGet(this::createSession);
    }

    private Session createSession() {
        final String id = String.valueOf(UUID.randomUUID());
        final Session session = new Session(id);
        SessionManager.add(session);
        return session;
    }

    public boolean hasCookie() {
        return headers.hasCookie();
    }

    public Cookie getCookie() {
        final String value = headers.getCookieValue();
        final String[] pair = value.split("=");
        return new Cookie(pair[0], pair[1]);
    }
}
