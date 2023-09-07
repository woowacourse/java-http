package org.apache.coyote.http11.request;

import static org.apache.coyote.http11.ContentType.HTML;
import static org.apache.coyote.http11.Header.ACCEPT;
import static org.apache.coyote.http11.Header.CONTENT_LENGTH;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.Cookie;
import org.apache.coyote.http11.Header;
import org.apache.coyote.http11.Session;
import org.apache.coyote.http11.SessionManager;

public class HttpRequest {

    private static final String JSESSIONID = "JSESSIONID";
    private final BufferedReader bufferedReader;
    private final Map<String, String> headers = new HashMap<>();
    private RequestLine requestLine;
    private final Map<String, String> bodies = new HashMap<>();
    private Cookie cookie;
    private Session session;

    public HttpRequest(BufferedReader bufferedReader) throws IOException {
        this.bufferedReader = bufferedReader;
        read();
    }

    private void read() throws IOException {
        String line;
        requestLine = RequestLine.of(bufferedReader.readLine());

        while (!(line = bufferedReader.readLine()).isBlank()) {
            putHeader(line);
        }
        if (headers.containsKey(CONTENT_LENGTH.getName())) {
            readBody();
        }
        if (headers.containsKey(Header.COOKIE.getName())) {
            this.cookie = new Cookie(headers.get(Header.COOKIE.getName()));
        }
        if (cookie != null) {
            this.session = (Session) SessionManager.getInstance().findSession(cookie.getValue(JSESSIONID));
        }
    }

    private void readBody() throws IOException {
        char[] chars = new char[getContentLength()];
        bufferedReader.read(chars, 0, getContentLength());
        putBody(new String(chars));
    }

    private void putBody(String line) {
        String[] split = line.split("&");
        for (String s : split) {
            String[] keyValue = s.split("=");
            bodies.put(keyValue[0], keyValue[1]);
        }
    }

    public void putHeader(String line) {
        if (line.isEmpty()) {
            return;
        }
        String[] split = line.split(": ");
        String name = split[0];
        String content = split[1];
        headers.put(name, content);
    }

    public String getAccept() {
        return headers.getOrDefault(ACCEPT.getName(), HTML.getType());
    }

    public int getContentLength() {
        return Integer.parseInt(headers.get(Header.CONTENT_LENGTH.getName()));
    }

    public String getBodyValue(String key) {
        return bodies.get(key);
    }

    public String getProtocolVersion() {
        return requestLine.getProtocolVersion();
    }

    public String getRequestUrl() {
        return requestLine.getUrl();
    }

    public String getMethod() {
        return requestLine.getMethod();
    }

    public Session getSession() {
        this.session = session == null ? Session.create() : session;
        return session;
    }

    public boolean methodEquals(String method) {
        return requestLine.getMethod().equals(method);
    }
}
