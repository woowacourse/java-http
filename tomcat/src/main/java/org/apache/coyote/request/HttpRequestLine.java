package org.apache.coyote.request;

public class HttpRequestLine {

    private static final String SEPERATOR = " ";
    private final String method;
    private final String requestUri;
    private final String version;

    private HttpRequestLine(String method, String requestUri, String version) {
        this.method = method;
        this.requestUri = requestUri;
        this.version = version;
    }

    public static HttpRequestLine from(String requestLine) {
        String[] splitedLines = requestLine.split(SEPERATOR);
        return new HttpRequestLine(splitedLines[0], splitedLines[1], splitedLines[2]);
    }

    public String getMethod() {
        return method;
    }

    public String getRequestUri() {
        return requestUri;
    }

    public boolean isGetMethod() {
        return method.equals("GET");
    }

    public boolean isPostMethod() {
        return method.equals("POST");
    }
}
