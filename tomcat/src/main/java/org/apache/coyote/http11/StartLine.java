package org.apache.coyote.http11;

public class StartLine {

    private static final String START_LINE_DELIMITER = " ";

    private final String httpMethod;
    private final String requestUri;
    private final String httpVersion;

    public StartLine(String httpMethod, String requestUri, String httpVersion) {
        this.httpMethod = httpMethod;
        this.requestUri = requestUri;
        this.httpVersion = httpVersion;
    }

    public static StartLine from(String startLine) {
        String[] splitStartLine = startLine.split(START_LINE_DELIMITER);

        return new StartLine(splitStartLine[0], splitStartLine[1], splitStartLine[2]);
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public String getRequestUri() {
        return requestUri;
    }

    public String getHttpVersion() {
        return httpVersion;
    }
}
