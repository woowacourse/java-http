package org.apache.coyote.http11;

public class HttpRequestFirstLine {

    private final String method;
    private final String url;
    private final String version;

    public HttpRequestFirstLine(String firstLine) {
        String[] split = firstLine.split(" ");
        method = split[0];
        url = split[1];
        version = split[2];
    }

    public String getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }
}
