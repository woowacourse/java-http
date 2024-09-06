package org.apache.coyote.http11;

public class HttpRequestFirstLine {

    private final HttpMethod method;
    private final String url;
    private final String version;

    public HttpRequestFirstLine(String firstLine) {
        String[] split = firstLine.split(" ");
        method = HttpMethod.valueOfMethod(split[0]);
        url = split[1];
        version = split[2];
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }
}
