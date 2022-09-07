package org.apache.coyote.http11.message.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.message.header.Header;
import org.apache.coyote.http11.message.request.body.RequestBody;
import org.apache.coyote.http11.message.request.header.Cookie;
import org.apache.coyote.http11.message.request.header.Headers;
import org.apache.coyote.http11.message.request.requestline.Method;
import org.apache.coyote.http11.message.request.requestline.RequestLine;

public class HttpRequest {

    private static final SessionManager SESSION_MANAGER = new SessionManager();

    private final RequestLine requestLine;
    private final Headers headers;
    private final RequestBody requestBody;

    public HttpRequest(final RequestLine requestLine, final Headers headers, final RequestBody requestBody) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.requestBody = requestBody;
    }

    public static HttpRequest from(final BufferedReader requestReader) throws IOException {
        final String rawRequestLine = Objects.requireNonNull(requestReader.readLine());
        final RequestLine requestLine = RequestLine.from(rawRequestLine);

        final List<String> rawHeaders = readHeaders(requestReader);
        final Headers headers = Headers.from(rawHeaders);

        final Optional<String> contentLength = headers.get(Header.CONTENT_LENGTH);
        if (contentLength.isPresent()) {
            final String rawContentLength = contentLength.get();
            final String body = readBody(requestReader, Integer.parseInt(rawContentLength.trim()));
            return new HttpRequest(requestLine, headers, RequestBody.from(body));
        }
        return new HttpRequest(requestLine, headers, RequestBody.ofEmpty());
    }

    private static List<String> readHeaders(final BufferedReader requestReader) throws IOException {
        final List<String> headers = new ArrayList<>();
        String line = requestReader.readLine();
        while (!"".equals(line)) {
            headers.add(line);
            line = requestReader.readLine();
        }
        return headers;
    }

    private static String readBody(final BufferedReader requestReader, final int contentLength) throws IOException {
        char[] buffer = new char[contentLength];
        requestReader.read(buffer, 0, contentLength);
        return new String(buffer);
    }

    public boolean isPath(final String path) {
        return requestLine.isPath(path);
    }

    public boolean isForResource() {
        return requestLine.isForResource();
    }

    public boolean isMethod(final Method method) {
        return requestLine.isMethod(method);
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public Session createSession() {
        final Session session = new Session();
        SESSION_MANAGER.add(session);
        return session;
    }

    public Optional<Object> getSessionAttribute(final String name) {
        final Session session = getSession();
        if (session == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(session.getAttribute(name));
    }

    private Session getSession() {
        final Cookie cookie = headers.getCookie();
        final Optional<String> jSessionId = cookie.getJSessionId();
        if (jSessionId.isEmpty()) {
            return null;
        }
        return SESSION_MANAGER.findSession(jSessionId.get());
    }

    public QueryParams getUriQueryParams() {
        return requestLine.getQueryParams();
    }

    public QueryParams getBodyQueryParams() {
        return requestBody.getQueryParams();
    }
}
