package org.apache.coyote.http11.httpmessage.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.coyote.http11.httpmessage.HttpCookie;
import org.apache.coyote.http11.httpmessage.HttpHeaders;
import org.apache.coyote.session.Session;
import org.apache.coyote.session.SessionManager;

public class HttpRequest {

    private final HttpRequestLine httpRequestLine;
    private final HttpHeaders headers;
    private final HttpCookie cookies;
    private final String body;

    private Session session;

    private HttpRequest(HttpRequestLine httpRequestLine, HttpHeaders headers, String body) {
        this.httpRequestLine = httpRequestLine;
        this.headers = headers;
        if (headers.contains(HttpHeaders.COOKIE)) {
            this.cookies = HttpCookie.parseFrom(headers.get(HttpHeaders.COOKIE));
        } else {
            this.cookies = new HttpCookie();
        }
        this.body = body;

        if (cookies.contains(HttpHeaders.JSESSIONID)) {
            SessionManager sessionManager = SessionManager.getInstance();
            this.session = sessionManager.findSession(cookies.getCookie(HttpHeaders.JSESSIONID));
        }
    }

    public static HttpRequest readFrom(BufferedReader reader) throws IOException {
        HttpRequestLine httpRequestLine = HttpRequestLine.parseFrom(reader.readLine());
        HttpHeaders header = new HttpHeaders(readHeader(reader));
        String requestBody = readBody(reader, header.getContentLength());
        return new HttpRequest(httpRequestLine, header, requestBody);
    }

    private static Map<String, String> readHeader(BufferedReader reader) throws IOException {
        String line;
        Map<String, String> headers = new HashMap<>();
        while (true) {
            line = reader.readLine();
            if (line == null || line.isBlank()) {
                break;
            }
            String[] headerLine = line.split(": ");
            headers.put(headerLine[0], headerLine[1]);
        }
        return headers;
    }

    private static String readBody(BufferedReader reader, int contentLength) throws IOException {
        char[] buffer = new char[contentLength];
        reader.read(buffer, 0, contentLength);
        return new String(buffer);
    }

    public Session getSession(boolean created) {
        if (created && this.session == null) {
            this.session = new Session(UUID.randomUUID().toString());
        }
        return this.session;
    }

    public boolean isGet() {
        return httpRequestLine.isGet();
    }

    public boolean isPost() {
        return httpRequestLine.isPost();
    }

    public HttpMethod getMethod() {
        return httpRequestLine.httpMethod();
    }

    public String getTarget() {
        return httpRequestLine.target();
    }

    public String getHttpVersion() {
        return httpRequestLine.httpVersion();
    }

    public String getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "Request{" +
                "requestLine=" + httpRequestLine +
                ", headers=" + headers +
                ", body='" + body + '\'' +
                '}';
    }
}
