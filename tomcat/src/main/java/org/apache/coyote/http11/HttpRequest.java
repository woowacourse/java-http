package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;

public class HttpRequest {

    private final RequestLine requestLine;
    private final Map<String, String> headers;
    private final Map<String, String> parameters;
    private final HttpCookie cookie;

    private Session session;
    private boolean newSession;

    private HttpRequest(RequestLine requestLine, Map<String, String> headers, String body) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.parameters = parseFormData(body);
        this.cookie = new HttpCookie(headers.get("Cookie"));
    }

    public static HttpRequest from(BufferedReader reader) throws IOException {
        RequestLine requestLine = new RequestLine(reader.readLine());
        Map<String, String> headers = readHeaders(reader);
        String body = readBody(reader, headers.get("Content-Length"));
        return new HttpRequest(requestLine, headers, body);
    }

    private static Map<String, String> readHeaders(BufferedReader reader) throws IOException {
        Map<String, String> headers = new HashMap<>();
        String line;
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            String[] keyValue = line.split(":", 2);
            if (keyValue.length == 2) {
                headers.put(keyValue[0].trim(), keyValue[1].trim());
            }
        }
        return headers;
    }

    private static String readBody(BufferedReader reader, String contentLength) throws IOException {
        if (contentLength == null) {
            return "";
        }
        int length = Integer.parseInt(contentLength);
        char[] buffer = new char[length];
        int read = 0;
        while (read < length) {
            int count = reader.read(buffer, read, length - read);
            if (count == -1) {
                break;
            }
            read += count;
        }
        return new String(buffer, 0, read);
    }

    private Map<String, String> parseFormData(String body) {
        Map<String, String> params = new HashMap<>();
        if (body == null || body.isEmpty()) {
            return params;
        }
        for (String pair : body.split("&")) {
            String[] keyValue = pair.split("=", 2);
            if (keyValue.length == 2) {
                params.put(keyValue[0], keyValue[1]);
            }
        }
        return params;
    }

    public Session getSession() {
        if (session != null) {
            return session;
        }

        SessionManager sessionManager = SessionManager.getInstance();
        if (cookie.hasJSessionId()) {
            session = sessionManager.findSession(cookie.getJSessionId());
        }
        if (session == null) {
            session = new Session(UUID.randomUUID().toString());
            sessionManager.add(session);
            newSession = true;
        }
        return session;
    }

    public boolean isNewSession() {
        return newSession;
    }

    public boolean isGet() {
        return requestLine.getMethod() == HttpMethod.GET;
    }

    public boolean isPost() {
        return requestLine.getMethod() == HttpMethod.POST;
    }

    public HttpMethod getMethod() {
        return requestLine.getMethod();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public String getHeader(String name) {
        return headers.get(name);
    }

    public String getParameter(String name) {
        return parameters.get(name);
    }

    public HttpCookie getCookie() {
        return cookie;
    }
}
