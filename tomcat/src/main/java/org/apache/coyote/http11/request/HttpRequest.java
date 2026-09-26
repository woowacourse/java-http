package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import org.apache.catalina.session.Session;

public class HttpRequest {

    private final RequestLine requestLine;
    private final HttpHeaders headers;
    private final RequestBody body;
    private final Session session;

    private HttpRequest(RequestLine requestLine, HttpHeaders headers, RequestBody body, Session session) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.body = body;
        this.session = session;
    }

    public static HttpRequest from(BufferedReader reader) throws IOException {
        RequestLine requestLine = RequestLine.from(reader.readLine());
        HttpHeaders headers = HttpHeaders.from(reader);
        RequestBody body = RequestBody.from(reader, headers.getContentLength());
        return new HttpRequest(requestLine, headers, body, null);
    }

    public HttpRequest withSession(Session session) {
        return new HttpRequest(requestLine, headers, body, session);
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
        String value = body.getParameter(name);
        if (value != null) {
            return value;
        }
        return requestLine.getQueryParameter(name);
    }

    public HttpCookie getCookie() {
        return headers.getCookie();
    }

    public Session getSession() {
        return session;
    }
}
