package org.apache.coyote.http11.request;

import java.util.List;

public class HttpRequest {

    private final RequestLine requestLine;

    public HttpRequest(RequestLine requestLine) {
        this.requestLine = requestLine;
    }

    public static HttpRequest of(List<String> requestLines) {
        validateRequest(requestLines);
        String firstLine = requestLines.getFirst();
        RequestLine requestLine = RequestLine.of(firstLine);

        return new HttpRequest(requestLine);
    }

    private static void validateRequest(List<String> requestLines) {
        System.out.println("requestLines = " + requestLines);
        if (requestLines.isEmpty()) {
            throw new IllegalArgumentException("올바르지 않은 HTTP 요청 형식입니다.");
        }
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public Queries getQueries() {
        return requestLine.getQueries();
    }

    public boolean isQueriesEmpty() {
        return getQueries().isEmpty();
    }
}
