package org.apache.coyote.http.request;

import java.util.List;

import static org.apache.coyote.util.Constants.CRLF;

public class HttpRequest {

    private static final int REQUEST_LINE_INDEX = 0;

    private final RequestLine requestLine;
    private final RequestHeader headers;
    private String body;

    public static HttpRequest of(String request) {
        if (request == null || request.isEmpty()) {
            throw new IllegalArgumentException("request cannot be null or empty");
        }
        List<String> requests = List.of(request.split(CRLF));
        return of(requests);
    }

    public static HttpRequest of(List<String> requests) {
        if (requests.isEmpty()) {
            throw new IllegalArgumentException("request cannot be null or empty");
        }
        return new HttpRequest(
                RequestLine.of(requests.get(REQUEST_LINE_INDEX)),
                new RequestHeader(requests.subList(REQUEST_LINE_INDEX + 1, requests.size())));
    }

    private HttpRequest(RequestLine requestLine, RequestHeader headers) {
        this.requestLine = requestLine;
        this.headers = headers;
    }

    public boolean isMethod(HttpMethod method) {
        return requestLine.getMethod().equals(method);
    }

    public String getMethodName() {
        return requestLine.getMethod().getMethodName();
    }

    public Path getPath() {
        return requestLine.getPath();
    }

    public boolean hasCookieWithSession() {
        return headers.hasCookieWithSession();
    }

    public int getContentLength() {
        return headers.getContentLength();
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
