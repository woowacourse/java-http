package org.apache.coyote.http11.common.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.apache.coyote.http11.common.Cookies;
import org.apache.coyote.http11.common.HttpHeaderName;
import org.apache.coyote.http11.common.HttpHeaders;
import org.apache.coyote.http11.common.MessageBody;
import org.apache.coyote.http11.common.Session;
import org.apache.coyote.http11.common.SessionManager;

public class HttpRequest {

    public static final String SESSIONID = "JSESSIONID";
    public static final String COOKIE = "Cookie";

    private final RequestLine requestLine;
    private final HttpHeaders headers;
    private final MessageBody body;
    private final SessionManager sessionManager;

    private HttpRequest(final RequestLine requestLine, final HttpHeaders headers, final MessageBody body) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.body = body;
        this.sessionManager = new SessionManager();
    }

    public static HttpRequest create(BufferedReader br) throws IOException {
        RequestLine requestLine = findStartLine(br);
        HttpHeaders headers = findHeaders(br);
        MessageBody body = findBody(headers, br);
        return new HttpRequest(requestLine, headers, body);
    }

    private static RequestLine findStartLine(BufferedReader br) throws IOException {
        String firstLine = br.readLine();
        return RequestLine.create(firstLine);
    }

    private static HttpHeaders findHeaders(BufferedReader br) throws IOException {
        List<String> headers = new ArrayList<>();
        String line = br.readLine();
        while (!"".equals(line)) {
            headers.add(line);
            line = br.readLine();
        }
        return HttpHeaders.create(headers);
    }

    private static MessageBody findBody(HttpHeaders headers, BufferedReader br) throws IOException {
        String contentLength = headers.getHeader(HttpHeaderName.CONTENT_LENGTH.getName());
        if (contentLength.isEmpty()) {
            return MessageBody.empty();
        }
        final int length = Integer.parseInt(contentLength);
        char[] buffer = new char[length];
        br.read(buffer, 0, length);
        return MessageBody.create(new String(buffer));
    }

    public RequestUri getUri() {
        return this.requestLine.getUri();
    }

    public HttpMethod getMethod() {
        return this.requestLine.getMethod();
    }

    public Cookies getCookies() {
        String cookieLine = headers.getHeader(COOKIE);
        if (cookieLine.isEmpty()) {
            return Cookies.empty();
        }
        return Cookies.create(cookieLine);
    }

    public MessageBody getBody() {
        return body;
    }

    public Optional<Session> getSession(boolean create) {
        String sessionId = getCookies().getCookie(SESSIONID);
        if (sessionId.isEmpty()) {
            if (create) {
                return Optional.of(makeNewSession());
            }
            return Optional.empty();
        }
        Optional<Session> session = sessionManager.findSession(sessionId);
        if (session.isEmpty()) {
            if (create) {
                return Optional.of(makeNewSession());
            }
            return Optional.empty();
        }
        return session;
    }

    private Session makeNewSession() {
        Session session = new Session(UUID.randomUUID().toString());
        sessionManager.add(session);
        return session;
    }
}
