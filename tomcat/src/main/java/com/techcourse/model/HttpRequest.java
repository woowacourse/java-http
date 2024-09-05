package com.techcourse.model;

import java.util.List;

public class HttpRequest {

    private final RequestLine requestLine;
    private final Header headers;

    public static HttpRequest of(String request) {
        List<String> requests = List.of(request.split("\r\n"));
        return new HttpRequest(RequestLine.of(requests.getFirst()), Header.of(requests.subList(1, requests.size())));
    }

    public static HttpRequest of(List<String> requests) {
        return new HttpRequest(RequestLine.of(requests.getFirst()), Header.of(requests.subList(1, requests.size())));
    }

    private HttpRequest(RequestLine requestLine, Header headers) {
        this.requestLine = requestLine;
        this.headers = headers;
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public Header getHeaders() {
        return headers;
    }
}
