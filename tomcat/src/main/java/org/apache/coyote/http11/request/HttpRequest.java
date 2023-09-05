package org.apache.coyote.http11.request;

import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionHolder;
import org.apache.coyote.http11.cookie.Cookie;
import org.apache.coyote.http11.response.header.Header;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class HttpRequest {

    private static final SessionHolder SESSION_MANAGER = new SessionHolder();

    private final Uri uri;
    private final Headers headers;
    private final Params params;
    private final Params body;

    private HttpRequest(final Uri uri, final Headers headers, final Params params, Params body) {
        this.uri = uri;
        this.headers = headers;
        this.params = params;
        this.body = body;
    }

    public static HttpRequest from(final BufferedReader reader) throws IOException {
        String rawRequestLine = Objects.requireNonNull(reader.readLine());
        Uri uri = Uri.from(rawRequestLine);

        List<String> headersRead = readHeader(reader);
        Headers headers = Headers.from(getHeaders(headersRead));

        Optional<String> contentLength = headers.getHeader(Header.CONTENT_LENGTH);
        if (contentLength.isPresent()) {
            String rawContentLength = contentLength.get();
            String body = getBody(reader, Integer.parseInt(rawContentLength.trim()));
            return getHttpRequest(uri, headers, Params.from(body));
        }

        return getHttpRequest(uri, headers, Params.createEmpty());
    }

    private static List<String> readHeader(final BufferedReader reader) throws IOException {
        List<String> headers = new ArrayList<>();
        String line = reader.readLine();
        while (!"".equals(line)) {
            headers.add(line);
            line = reader.readLine();
        }
        return headers;
    }

    private static List<String> getHeaders(final List<String> request) {
        return request.subList(1, request.size());
    }

    private static String getBody(final BufferedReader requestReader, final int contentLength) throws IOException {
        char[] buffer = new char[contentLength];
        requestReader.read(buffer, 0, contentLength);
        return new String(buffer);
    }

    private static HttpRequest getHttpRequest(final Uri uri, final Headers headers, final Params body) {
        if (!uri.hasQueryParams()) {
            return new HttpRequest(uri, headers, Params.createEmpty(), body);
        }

        String query = uri.getQueryParams();
        return new HttpRequest(uri, headers, Params.from(query), body);
    }

    public boolean isSamePath(final String path) {
        return this.uri.isSamePath(path);
    }

    public boolean hasResource() {
        return uri.hasResource();
    }

    public Session createSession() {
        Session session = new Session();
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
        return jSessionId.map(SESSION_MANAGER::findSession)
                .orElse(null);
    }

    public boolean isGetMethod() {
        return uri.isGetMethod();
    }

    public boolean isPostMethod() {
        return uri.isPostMethod();
    }

    public HttpMethod getHttpMethod() {
        return uri.getHttpMethod();
    }

    public String getPath() {
        return uri.getPath();
    }

    public Params getParams() {
        return params;
    }

    public Params getBody() {
        return body;
    }
}
