package org.apache.coyote.http11;

public class RequestLine {
    private static final String REQUEST_HEADER_DELIMITER = " ";
    private final String httpMethod;
    private final RequestURI requestURI;
    private final String httpVersion;

    public RequestLine(final String httpMethod, final RequestURI requestURI, final String httpVersion) {
        this.httpMethod = httpMethod;
        this.requestURI = requestURI;
        this.httpVersion = httpVersion;
    }

    public static RequestLine from(final String requestHeaderFirstLine) {
        final String[] splitedLine = requestHeaderFirstLine.split(REQUEST_HEADER_DELIMITER);
        return new RequestLine(
                splitedLine[0],
                new RequestURI(splitedLine[1]),
                splitedLine[2]
        );
    }

    public boolean isExistRequestFile() {
        return this.requestURI.isExistFile();
    }

    public String getPath() {
        return this.requestURI.getPath();
    }

    public String readFile() {
        return this.requestURI.readFile();
    }

    public RequestURI getRequestURI() {
        return requestURI;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public String getHttpVersion() {
        return httpVersion;
    }
}
