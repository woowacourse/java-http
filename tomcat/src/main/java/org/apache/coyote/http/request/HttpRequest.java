package org.apache.coyote.http.request;

import org.apache.coyote.http.HttpCookie;

import java.util.List;

import static org.apache.coyote.util.Constants.CRLF;

public class HttpRequest {

    private final RequestLine requestLine;
    private final RequestHeader headers;
    private String body;

    public static HttpRequest of(String request) {
        List<String> requests = List.of(request.split(CRLF));
        return of(requests);
    }

    public static HttpRequest of(List<String> requests) {
        return new HttpRequest(
                RequestLine.of(requests.getFirst()),
                new RequestHeader(requests.subList(1, requests.size())));
    }

    private HttpRequest(RequestLine requestLine, RequestHeader headers) {
        this.requestLine = requestLine;
        this.headers = headers;
    }

    public HttpMethod getMethod() {
        return requestLine.getMethod();
    }

    public Path getPath() {
        return requestLine.getPath();
    }

    public HttpCookie getCookie() {
        return headers.getCookie();
    }

    public int getContentLength() {
        return Integer.parseInt(headers.getContentLength());
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
