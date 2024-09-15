package org.apache.catalina.request;

public class StartLine {

    public static final String START_LINE_DELIMITER = " ";

    private final HttpMethod httpMethod;
    private final String uri;

    private StartLine(HttpMethod httpMethod, String uri) {
        this.httpMethod = httpMethod;
        this.uri = uri;
    }

    public static StartLine parse(String rawStartLine) {
        String[] splits = rawStartLine.split(START_LINE_DELIMITER);
        return new StartLine(HttpMethod.valueOf(splits[0]), splits[1]);
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getUri() {
        return uri;
    }
}
