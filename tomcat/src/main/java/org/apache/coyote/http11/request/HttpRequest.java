package org.apache.coyote.http11.request;

import static org.apache.coyote.http11.support.QueryStringParser.parseQueryString;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        final HttpHeaders httpHeaders = toHttpHeaders(headerLines);

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

    private static HttpHeaders toHttpHeaders(final List<String> values) {
        final Map<String, String> headers = new HashMap<>();
        for (String value : values) {
            final String[] header = value.split(": ");
            headers.put(header[0], header[1]);
        }
        final HttpHeaders httpHeaders = new HttpHeaders(headers);
        return httpHeaders;
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
