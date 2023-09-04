package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.apache.coyote.http11.common.HttpHeaders;
import org.apache.coyote.http11.common.MessageBody;
import org.apache.coyote.http11.common.Session;
import org.apache.coyote.http11.common.SessionManger;

public class HttpRequest {
    private final StartLine startLine;
    private final HttpHeaders headers;
    private final MessageBody body;
    private final SessionManger sessionManger;

    private HttpRequest(final StartLine startLine, final HttpHeaders headers, final MessageBody body) {
        this.startLine = startLine;
        this.headers = headers;
        this.body = body;
        this.sessionManger = new SessionManger();
    }

    public static HttpRequest create(BufferedReader br) throws IOException {
        StartLine startLine = findStartLine(br);
        HttpHeaders headers = findHeaders(br);
        MessageBody body = findBody(headers, br);
        return new HttpRequest(startLine, headers, body);
    }

    private static StartLine findStartLine(BufferedReader br) throws IOException {
        String firstLine = br.readLine();
        return StartLine.create(firstLine);
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
        final String contentLength = headers.getHeader("Content-Length");
        if (contentLength == null) {
            return MessageBody.empty();
        }
        final int length = Integer.parseInt(contentLength);
        char[] buffer = new char[length];
        br.read(buffer, 0, length);
        return MessageBody.create(new String(buffer));
    }

    public RequestUri getUri() {
        return this.startLine.getUri();
    }

    public HttpMethod getMethod() {
        return this.startLine.getMethod();
    }

    public MessageBody getBody() {
        return body;
    }

    public Session getSession() {
        Session session = null;
        try {
            String sessionId = headers.getCookies().getCookie("JSESSIONID");
            session = sessionManger.findSession(sessionId);
        } catch (Exception e) {
            return makeNewSession();
        }
        return session;
    }

    private Session makeNewSession() {
        Session session = new Session(UUID.randomUUID().toString());
        sessionManger.add(session);
        return session;
    }
}
