package org.apache.coyote.http11;


import java.util.List;

public class Request {

    private static final int START_LINE_INDEX = 0;
    private static final int HEADER_INDEX = 1;

    private final String method;
    private final String httpVersion;
    private final RequestURI requestURI;
    private final Headers headers;

    public static Request from(List<String> requestLines) {
        String[] splitStartLine = requestLines.get(START_LINE_INDEX).split(" ");
        String method = splitStartLine[0];
        String requestURI = splitStartLine[1];
        String httpVersion = splitStartLine[2];
        List<String> headers = requestLines.subList(HEADER_INDEX, requestLines.size() - 1);

        return new Request(method, httpVersion, requestURI, headers);
    }

    public Request(String method, String httpVersion, String requestURI, List<String> headers) {
        this.method = method;
        this.httpVersion = httpVersion;
        this.requestURI = RequestURI.from(requestURI);
        this.headers = Headers.from(headers);
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
}
