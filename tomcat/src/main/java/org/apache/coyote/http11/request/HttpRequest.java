package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Objects;

public class HttpRequest {

    private final RequestLine requestLine;
    private final RequestHeader requestHeader;

    private HttpRequest(RequestLine requestLine, RequestHeader requestHeader) {
        this.requestLine = requestLine;
        this.requestHeader = requestHeader;
    }

    public static HttpRequest of(String request) {
        LinkedList<String> lines = getLines(request);

        RequestLine requestLine = RequestLine.of(lines.remove());
        RequestHeader requestHeader = RequestHeader.of(lines);

        return new HttpRequest(requestLine, requestHeader);
    }

    private static LinkedList<String> getLines(String request) {
        return new LinkedList<>(Arrays.asList(request.split("\n")));
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public RequestHeader getRequestHeader() {
        return requestHeader;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        HttpRequest that = (HttpRequest) o;
        return Objects.equals(requestLine, that.requestLine) && Objects.equals(requestHeader,
                that.requestHeader);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requestLine, requestHeader);
    }
}
