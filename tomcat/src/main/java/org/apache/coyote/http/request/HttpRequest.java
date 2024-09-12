package org.apache.coyote.http.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http.HttpCookie;
import org.apache.coyote.http.HttpMethod;

public class HttpRequest {

    private static final String REQUEST_HEADER_SUFFIX = "";
    private static final String COOKIE_NAME_SESSION_ID = "JSESSIONID";

    private final HttpRequestStartLine startLine;
    private final HttpRequestHeader header;
    private final HttpRequestBody body;
    private final SessionManager sessionManager = new SessionManager(); // TODO: catalina 계층의 request 분리하기(coyoteAdaptor)

    public HttpRequest(final BufferedReader bufferedReader) throws IOException {
        this.startLine = readStartLine(bufferedReader);
        this.header = readHeader(bufferedReader);
        this.body = readBody(bufferedReader);
    }

    private HttpRequestStartLine readStartLine(final BufferedReader bufferedReader) throws IOException {
        return Optional.ofNullable(bufferedReader.readLine())
                .filter(line -> !line.isEmpty())
                .map(HttpRequestStartLine::new)
                .orElseThrow(() -> new IllegalArgumentException("요청의 시작줄이 비어있습니다."));
    }

    private HttpRequestHeader readHeader(final BufferedReader bufferedReader) throws IOException {
        final List<String> lines = new ArrayList<>();
        String line;
        while (!REQUEST_HEADER_SUFFIX.equals(line = bufferedReader.readLine())) {
            lines.add(line);
        }
        return new HttpRequestHeader(lines);
    }

    private HttpRequestBody readBody(final BufferedReader bufferedReader) throws IOException {
        final int contentLength = header.getContentLength();
        final char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        return new HttpRequestBody(new String(buffer));
    }

    public Session createSession() {
        Session session = new Session();
        sessionManager.add(session);
        return session;
    }

    public Session getSession() {
        return Optional.ofNullable(header.getCookie(COOKIE_NAME_SESSION_ID))
                .map(HttpCookie::getValue)
                .map(sessionManager::findSession)
                .orElse(null);
    }

    public HttpMethod getHttpMethod() {
        return startLine.getHttpMethod();
    }

    public String getRequestURI() {
        return startLine.getRequestURI();
    }

    public HttpRequestBody getBody() {
        return body;
    }
}
