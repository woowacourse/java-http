package org.apache.coyote.http11.httpmessage;


import java.util.List;

public class Request {

    private final String method;
    private final String httpVersion;
    private final RequestURI requestURI;
    private final Headers headers;
    private final Body body;

    public Request(String method, String httpVersion,
                   String requestURI, List<String> headers, List<String> body) {
        this.method = method;
        this.httpVersion = httpVersion;
        this.requestURI = RequestURI.from(requestURI);
        this.headers = Headers.from(headers);
        this.body = Body.from(body);
    }

    public static Request from(String startLine, List<String> headers, List<String> body) {
        String[] splitStartLine = startLine.split(" ");
        String method = splitStartLine[0];
        String requestURI = splitStartLine[1];
        String httpVersion = splitStartLine[2];

        return new Request(method, httpVersion, requestURI, headers, body);
    }

    public String getMethod() {
        return method;
    }

    public String getHttpVersion() {
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
