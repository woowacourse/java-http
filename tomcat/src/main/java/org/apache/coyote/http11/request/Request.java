package org.apache.coyote.http11.request;

import java.util.List;

public class Request {

    private final RequestLine requestLine;

    private Request(RequestLine requestLine) {
        this.requestLine = requestLine;
    }

    public static Request parseFrom(List<String> request) {
        return new Request(RequestLine.parseFrom(request.getFirst()));
    }

    public Method getMethod() {
        return requestLine.method();
    }

    public String getTarget() {
        return requestLine.target();
    }

    public String getHttpVersion() {
        return requestLine.httpVersion();
    }

    @Override
    public String toString() {
        return "Request{" +
                "requestLine=" + requestLine +
                '}';
    }
}
