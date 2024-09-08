package org.apache.catalina.request;

public class StartLine {

    public static final String START_LINE_DELIMITER = " ";

    private final HttpMethod httpMethod;
    private final String url;

    private StartLine(HttpMethod httpMethod, String url) {
        this.httpMethod = httpMethod;
        this.url = url;
    }

    public static StartLine parse(String rawStartLine) {
        String[] splits = rawStartLine.split(START_LINE_DELIMITER);
        return new StartLine(HttpMethod.valueOf(splits[0]), splits[1]);
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getUrl() {
        return url;
    }
}
