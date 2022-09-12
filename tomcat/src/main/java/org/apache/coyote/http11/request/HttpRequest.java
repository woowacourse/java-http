package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import org.apache.catalina.HttpSession;
import org.apache.catalina.SessionManager;

public class HttpRequest {
    private final RequestLine requestLine;
    private final HttpHeaders httpHeaders;
    private final HttpCookie cookie;
    private final String requestBody;

    public HttpRequest(RequestLine requestLine, HttpHeaders httpHeaders, HttpCookie cookie,
                       String requestBody) {
        this.requestLine = requestLine;
        this.httpHeaders = httpHeaders;
        this.cookie = cookie;
        this.requestBody = requestBody;
    }

    public static HttpRequest from(BufferedReader bufferedReader) throws IOException {
        RequestLine requestLine = RequestLine.extract(bufferedReader.readLine());
        HttpHeaders httpHeaders = HttpHeaders.create(bufferedReader);
        String requestBody = extractRequestBody(bufferedReader, requestLine, httpHeaders);
        return new HttpRequest(requestLine, httpHeaders, HttpCookie.extract(httpHeaders), requestBody);
    }

    private static String extractRequestBody(BufferedReader bufferedReader, RequestLine requestLine,
                                             HttpHeaders httpHeaders) throws IOException {
        if (requestLine.getHttpMethod().equals(HttpMethod.POST)) {
            int contentLength = Integer.parseInt(httpHeaders.get("Content-Length"));
            char[] buffer = new char[contentLength];
            bufferedReader.read(buffer, 0, contentLength);
            return new String(buffer);
        }
        return "";
    }

    public HttpSession getSession() {
        SessionManager sessionManager = new SessionManager();
        String session = cookie.getSession();
        if (session == null) {
            return new HttpSession();
        }
        return sessionManager.findSession(session);
    }

    public HttpMethod getHttpMethod() {
        return requestLine.getHttpMethod();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public String getRequestBody() {
        return requestBody;
    }

    @Override
    public String toString() {
        return "HttpRequest{" +
                "requestLine=" + requestLine +
                ", httpHeaders=" + httpHeaders +
                ", cookie=" + cookie +
                ", requestBody='" + requestBody + '\'' +
                '}';
    }
}
