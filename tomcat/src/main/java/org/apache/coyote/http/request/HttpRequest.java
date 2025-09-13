package org.apache.coyote.http.request;

import jakarta.servlet.http.Cookie;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

public class HttpRequest {

    private final RequestLine requestLine;
    private final Map<String, String> headers;
    private final Map<String, String> body;
    private final Cookie cookie;

    private HttpRequest(
            final RequestLine requestLine,
            final Map<String, String> headers,
            final Map<String, String> body,
            final Cookie cookie
    ) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.body = body;
        this.cookie = cookie;
    }

    public static HttpRequest from(final BufferedReader bufferedReader) throws IOException {
        final RequestLine requestLine = RequestParser.parseRequestLine(bufferedReader);
        final Map<String, String> headers = RequestParser.parseHeaders(bufferedReader);
        final Cookie cookie = RequestParser.parseCookie(headers);
        final Map<String, String> body = RequestParser.parseBody(headers, bufferedReader);

        return new HttpRequest(requestLine, headers, body, cookie);
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public Map<String, String> getBody() {
        return Collections.unmodifiableMap(body);
    }

    public Cookie getCookie() {
        return cookie;
    }
}
