package org.apache.coyote.request;


public class HttpRequestLine {

    private static final String SEPERATOR = " ";
    private final String method;
    private final RequestUri requestUri;
    private final String version;

    public HttpRequestLine(String method, RequestUri requestUri, String version) {
        this.method = method;
        this.requestUri = requestUri;
        this.version = version;
    }

    public static HttpRequestLine from(String requestLine) {
        String[] splitedLines = requestLine.split(SEPERATOR);

        return new HttpRequestLine(splitedLines[0], RequestUri.from(splitedLines[1]), splitedLines[2]);
    }

    public RequestUri getRequestUri() {
        return requestUri;
    }

    public boolean isGetMethod() {
        return method.equals("GET");
    }

    public boolean isPostMethod() {
        return method.equals("POST");
    }
}
