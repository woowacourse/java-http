package org.apache.coyote.http11.httpmessage;


import java.util.List;

public class Request {

    private final HttpMethod method;
    private final HttpVersion httpVersion;
    private final RequestURI requestURI;
    private final Headers headers;
    private final Body body;

    private Request(HttpMethod method, HttpVersion httpVersion,
                    RequestURI requestURI, Headers headers, Body body) {
        this.method = method;
        this.httpVersion = httpVersion;
        this.requestURI = requestURI;
        this.headers = headers;
        this.body = body;
    }

    public static Request from(String startLine, List<String> headers, List<String> body) {
        String[] splitStartLine = startLine.split(" ");
        String method = splitStartLine[0];
        String requestURI = splitStartLine[1];
        String httpVersion = splitStartLine[2];

        return new Request(HttpMethod.from(method), HttpVersion.from(httpVersion),
                RequestURI.from(requestURI), Headers.from(headers), Body.from(body));
    }

    public HttpMethod getMethod() {
        return method;
    }

    public HttpVersion getHttpVersion() {
        return httpVersion;
    }

    public RequestURI getRequestURI() {
        return requestURI;
    }

    public Headers getHeaders() {
        return headers;
    }

    public Body getBody() {
        return body;
    }
}
