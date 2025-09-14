package org.apache.coyote.http11.model;

import org.apache.coyote.http11.session.Session;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;

public class HttpRequest {

    private final RequestLine requestLine;
    private final Headers headers;
    private final String body;
    private Session session;

    private HttpRequest(final RequestLine requestLine, final Headers headers, final String body) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.body = body;
    }

    public static HttpRequest from(final InputStream inputStream) throws IOException {
        final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        final var requestLineString = bufferedReader.readLine();

        if (requestLineString == null || requestLineString.isBlank()) {
            throw new IllegalArgumentException("Empty request line");
        }

        final RequestLine requestLine = RequestLine.from(requestLineString);
        final Headers headers = Headers.from(bufferedReader);
        final String body = getBody(headers, bufferedReader);

        if (body != null && Objects.equals(headers.getHeaderValue("Content-Type"), "application/x-www-form-urlencoded")) {
            requestLine.mergeToQueryParameter(body);
        }

        return new HttpRequest(requestLine, headers, body);
    }

    private static String getBody(final Headers headers, final BufferedReader bufferedReader) throws IOException {
        if (headers.containsKey("Content-Length")) {
            int contentLength = Integer.parseInt(headers.getHeaderValue("Content-Length"));

            char[] bodyChars = new char[contentLength];
            int read = bufferedReader.read(bodyChars);

            return new String(bodyChars, 0, read);
        }
        return null;
    }

    public HttpMethod getMethod() {
        return requestLine.getHttpMethod();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public String getQueryParameter(String key) {
        return requestLine.getQueryParameterValue(key);
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public String getHeader(String name) {
        return headers.getHeaderValue(name);
    }

    public Cookie getCookies() {
        return new Cookie(getHeader("Cookie"));
    }

    public Session getSession() {
        return session;
    }
}
