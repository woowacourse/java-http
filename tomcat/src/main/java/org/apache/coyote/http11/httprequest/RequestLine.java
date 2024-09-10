package org.apache.coyote.http11.httprequest;

public class RequestLine {

    private static final String REQUEST_LINE_DELIMITER = " ";

    private final HttpMethod httpMethod;
    private final String uri;
    private final String httpVersion;

    public RequestLine(String requestLine) {
        String[] parts = requestLine.split(REQUEST_LINE_DELIMITER);
        this.httpMethod = HttpMethod.from(parts[0]);
        this.uri = parts[1];
        this.httpVersion = parts[2];
    }

    public boolean isGetMethod() {
        return httpMethod.isGet();
    }

    public boolean isPostMethod() {
        return httpMethod.isPost();
    }

    public String getPath() {
        int index = uri.indexOf("?");
        if (index == -1) {
            return uri;
        }
        return uri.substring(0, index);
    }

    public String getUri() {
        return uri;
    }
}
