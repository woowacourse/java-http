package org.apache.coyote.http11.request;

import org.apache.coyote.http11.common.HttpMethod;
import org.apache.coyote.http11.cookie.Session;
import org.apache.coyote.http11.cookie.SessionManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class HttpRequest {

    private final RequestLine requestLine;
    private final RequestHeaders requestHeaders;
    private final RequestBody requestBody;

    private HttpRequest(RequestHeaders requestHeaders, RequestLine requestLine, RequestBody requestBody) {
        this.requestLine = requestLine;
        this.requestHeaders = requestHeaders;
        this.requestBody = requestBody;
    }

    public static HttpRequest parse(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        RequestLine requestLine = RequestLine.parse(bufferedReader.readLine());
        RequestHeaders requestHeaders = RequestHeaders.parse(bufferedReader);
        RequestBody requestBody = parseBody(requestHeaders, bufferedReader);
        return new HttpRequest(requestHeaders, requestLine, requestBody);
    }

    private static RequestBody parseBody(RequestHeaders requestHeaders, BufferedReader bufferedReader) throws IOException {
        if (requestHeaders.hasNotBody()) {
            return RequestBody.EMPTY;
        }
        int contentLength = Integer.parseInt(requestHeaders.getHeader("Content-Length"));
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        String body = new String(buffer);
        return RequestBody.parse(body);
    }

    public String getBody(String key) {
        return requestBody.getBody(key);
    }

    public String getQueryParam(String key) {
        return requestLine.getQueryParam(key);
    }

    public HttpMethod getMethod() {
        return requestLine.getMethod();
    }

    public String getRequestUri() {
        return requestLine.getRequestUri();
    }

    public RequestHeaders getRequestHeaders() {
        return requestHeaders;
    }

    public boolean existsSession() {
        return requestHeaders.getCookies().existsSession();
    }

    public boolean isMatchMethod(HttpMethod httpMethod) {
        return requestLine.isMatchMethod(httpMethod);
    }

    public boolean isStartsWith(String path) {
        return requestLine.getRequestUri().startsWith(path);
    }

    public Session getSession(boolean create) {
        if (!create) {
            return SessionManager.findSession(requestHeaders.getCookies().getCookieValue("JSESSIONID"));
        }
        Session session = Session.uuid();
        SessionManager.add(session);
        return session;
    }

    @Override
    public String toString() {
        return "HttpRequest{" +
                "requestLine=" + requestLine +
                ", requestHeaders=" + requestHeaders +
                ", requestBody=" + requestBody +
                '}';
    }
}
