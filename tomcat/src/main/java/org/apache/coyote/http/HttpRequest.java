package org.apache.coyote.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.StringJoiner;

import org.apache.catalina.session.Session;

public class HttpRequest implements HttpComponent {

    private final HttpRequestLine requestLine;
    private final HttpHeaders headers;
    private final HttpBody body;

    public HttpRequest(final InputStream inputStream) throws IOException {
        String request = inputAsString(inputStream);
        this.requestLine = new HttpRequestLine(request);
        this.headers = new HttpHeaders(request);
        String[] parts = request.split("\r\n\r\n", 2);
        this.body = new HttpBody(parts[1]);
    }

    private String inputAsString(final InputStream inputStream) throws IOException {
        var bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        var stringBuilder = new StringBuilder();
        var buffer = new char[inputStream.available()];
        int readCount = bufferedReader.read(buffer, 0, inputStream.available());
        stringBuilder.append(buffer, 0, readCount);
        return stringBuilder.toString();
    }

    public HttpMethod getMethod() {
        return requestLine.getMethod();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public HttpQueryParams getQueryParams() {
        return requestLine.getQueryParams();
    }

    public HttpVersion getHttpVersion() {
        return requestLine.getVersion();
    }

    public String getAccept() {
        return headers.get(HttpHeaders.ACCEPT);
    }

    public HttpCookies getCookies() {
        return headers.getCookies();
    }

    public Session getSession(boolean create) {
        String sessionId = headers.getSessionId();
        Session session = null;
        if (sessionId != null) {
            session = new Session(sessionId);
        }
        if (create && session == null) {
            session = new Session();
        }
        return session;
    }

    public HttpBody getBody() {
        return body;
    }

    @Override
    public String asString() {
        final var result = new StringJoiner(LINE_FEED);
        return result.add(requestLine.asString())
                .add(headers.asString())
                .add(body.asString())
                .toString();
    }
}
