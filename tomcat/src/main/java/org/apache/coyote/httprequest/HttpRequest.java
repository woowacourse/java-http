package org.apache.coyote.httprequest;

import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;
import org.apache.coyote.httprequest.header.RequestHeaders;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.UUID;

public class HttpRequest {

    private static final String HEADER_LINE_DELIMITER = "\n";

    private final HttpRequestLine httpRequestLine;
    private final RequestHeaders requestHeaders;
    private final RequestBody requestBody;

    private HttpRequest(final HttpRequestLine httpRequestLine, final RequestHeaders requestHeaders, final RequestBody requestBody) {
        this.httpRequestLine = httpRequestLine;
        this.requestHeaders = requestHeaders;
        this.requestBody = requestBody;
    }

    public static HttpRequest from(final InputStream inputStream) throws IOException {
        final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        final HttpRequestLine httpRequestLine = makeHttpRequestLine(bufferedReader.readLine());
        final RequestHeaders requestHeaders = makeRequestHeaders(bufferedReader);
        final RequestBody requestBody = makeRequestBody(bufferedReader, requestHeaders.getContentLength());
        return new HttpRequest(httpRequestLine, requestHeaders, requestBody);
    }

    private static HttpRequestLine makeHttpRequestLine(final String line) {
        return HttpRequestLine.from(line);
    }

    private static RequestHeaders makeRequestHeaders(final BufferedReader bufferedReader) throws IOException {
        final StringBuilder stringBuilder = new StringBuilder();
        String line = bufferedReader.readLine();
        while (isAvailableLine(line)) {
            stringBuilder.append(line).append(HEADER_LINE_DELIMITER);
            line = bufferedReader.readLine();
        }
        return RequestHeaders.from(stringBuilder.toString(), HEADER_LINE_DELIMITER);
    }

    private static boolean isAvailableLine(final String line) {
        return !(line == null || line.isBlank());
    }

    private static RequestBody makeRequestBody(final BufferedReader bufferedReader, final int contentLength) throws IOException {
        final char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        final String content = new String(buffer);
        return new RequestBody(content);
    }

    public boolean hasQueryString() {
        return !getQueryString().isEmpty();
    }

    public boolean hasJSessionId() {
        return requestHeaders.hasJSessionId();
    }

    public String getPath() {
        return httpRequestLine.getPath();
    }

    public QueryString getQueryString() {
        return httpRequestLine.getQueryString();
    }

    public String getHttpVersion() {
        return httpRequestLine.getHttpVersion();
    }

    public RequestMethod getRequestMethod() {
        return httpRequestLine.getRequestMethod();
    }

    public RequestBody getRequestBody() {
        return requestBody;
    }

    public Session getSession(final boolean create) {
        final SessionManager sessionManager = SessionManager.getInstance();
        final String jSessionId = requestHeaders.getJSessionId();
        if (jSessionId == null) {
            return createSession(sessionManager);
        }
        final Session session = sessionManager.findSession(jSessionId);
        if (session == null && create) {
            return createSession(sessionManager);
        }
        return session;
    }

    private Session createSession(final SessionManager sessionManager) {
        final Session newSession = new Session(UUID.randomUUID().toString());
        sessionManager.add(newSession);
        return newSession;
    }
}
