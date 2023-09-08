package org.apache.coyote.http11.request;

import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionHolder;
import org.apache.coyote.http11.cookie.Cookie;
import org.apache.coyote.http11.request.body.Params;
import org.apache.coyote.http11.request.header.Headers;
import org.apache.coyote.http11.request.uri.HttpMethod;
import org.apache.coyote.http11.request.uri.Uri;
import org.apache.coyote.http11.response.header.Header;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static org.apache.coyote.http11.common.Constant.BLANK;

public class HttpRequest {

    private static final SessionHolder SESSION_MANAGER = new SessionHolder();

    private final Uri uri;
    private final Headers headers;
    private final Params body;

    private HttpRequest(final Uri uri, final Headers headers, Params body) {
        this.uri = uri;
        this.headers = headers;
        this.body = body;
    }

    public static HttpRequest from(final BufferedReader reader) throws IOException {
        String rawRequestLine = Objects.requireNonNull(reader.readLine());
        List<String> headersRead = readHeader(reader);

        Uri uri = Uri.from(rawRequestLine);
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
        while (!line.equals(BLANK)) {
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
            return new HttpRequest(uri, headers, body);
        }

        String query = uri.getQueryParams();
        return new HttpRequest(uri, headers, Params.from(query));
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
        return getSession().map(session -> session.getAttribute(name));
    }

    private Optional<Session> getSession() {
        Cookie cookie = headers.getCookie();
        Optional<String> jSessionId = cookie.getJSessionId();
        return jSessionId.map(SESSION_MANAGER::findSession);
    }

    public boolean isSameMethod(final HttpMethod httpMethod) {
        return uri.isSameMethod(httpMethod);
    }

    public HttpMethod getHttpMethod() {
        return uri.getHttpMethod();
    }

    public String getPath() {
        return uri.getPath();
    }

    public Map<String, String> getParams() {
        return body.getParams();
    }
}
