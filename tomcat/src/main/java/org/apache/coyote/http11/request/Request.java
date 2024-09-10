package org.apache.coyote.http11.request;

import java.util.List;

public class Request {

    private final RequestLine requestLine;
    private final String body;

    private Request(RequestLine requestLine, String body) {
        this.requestLine = requestLine;
        this.body = body;
    }

    public static Request parseFrom(List<String> request) {
        RequestLine requestLine = RequestLine.parseFrom(request.getFirst());
        StringBuilder bodyBuilder = new StringBuilder();

        int emptyLineIndex = request.indexOf("");
        for (int index = emptyLineIndex + 1; index < request.size(); index++) {
            bodyBuilder.append(request.get(index)).append("\r\n");
        }
        return new Request(requestLine, bodyBuilder.toString());
    }

    public boolean isPost() {
        return requestLine.isPost();
    }

    public boolean isStaticResourceRequest() {
        return requestLine.isStaticResourceRequest();
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

    public String getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "Request{" +
                "requestLine=" + requestLine +
                ", body='" + body + '\'' +
                '}';
    }
}
