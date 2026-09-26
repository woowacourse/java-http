package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;

public class HttpRequest {

    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String COOKIE = "Cookie";
    private static final String JSESSIONID = "JSESSIONID";

    private final RequestLine requestLine;
    private final Map<String, String> headers;
    private final String body;

    private Session session;

    private HttpRequest(RequestLine requestLine, Map<String, String> headers, String body) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.body = body;
    }

    public static HttpRequest from(BufferedReader reader) throws IOException {
        String requestLine = reader.readLine();
        if (requestLine == null || requestLine.isBlank()) {
            throw new IllegalArgumentException("[ERROR] 요청 라인이 비어 있습니다.");
        }

        Map<String, String> headers = readHeaders(reader);
        String body = readBody(reader, headers);

        return new HttpRequest(RequestLine.from(requestLine), headers, body);
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public boolean isGet() {
        return requestLine.isGet();
    }

    public boolean isPost() {
        return requestLine.isPost();
    }

    public RequestParameters getQueryParameters() {
        return requestLine.getQueryParameters();
    }

    public RequestParameters getFormData() {
        return RequestParameters.from(body);
    }

    public HttpCookie getCookie() {
        return HttpCookie.from(headers.get(COOKIE));
    }

    private static Map<String, String> readHeaders(BufferedReader reader) throws IOException {
        Map<String, String> headers = new HashMap<>();

        String line;
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            int colonIndex = line.indexOf(":");
            if (colonIndex == -1) {
                continue;
            }

            String name = line.substring(0, colonIndex).trim();
            String value = line.substring(colonIndex + 1).trim();
            headers.put(name, value);
        }
        return headers;
    }

    private static String readBody(BufferedReader reader, Map<String, String> headers) throws IOException {
        String contentLength = headers.get(CONTENT_LENGTH);
        if (contentLength == null) {
            return "";
        }

        int length = Integer.parseInt(contentLength);
        char[] buffer = new char[length];
        reader.read(buffer, 0, length);

        return new String(buffer);
    }

    public Session getSession(boolean create) {
        if (session != null) {
            return session;
        }

        Optional<String> sessionId = getCookie().getValue(JSESSIONID);
        if (sessionId.isPresent()) {
            session = SessionManager.getInstance().findSession(sessionId.get());
            if (session != null) {
                return session;
            }
        }

        if (!create) {
            return null;
        }

        session = new Session(UUID.randomUUID().toString());
        SessionManager.getInstance().add(session);
        return session;
    }
}
