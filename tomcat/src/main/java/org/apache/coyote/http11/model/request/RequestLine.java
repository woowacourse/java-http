package org.apache.coyote.http11.model.request;

public class RequestLine {

    private static final String SEPARATOR = " ";
    private static final int METHOD_INDEX = 0;
    private static final int URL_INDEX = 1;

    private final String method;
    private final String url;

    public RequestLine(final String line) {
        String[] split = line.split(SEPARATOR);

        this.method = split[METHOD_INDEX];
        this.url = split[URL_INDEX];
    }

    public String getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }
}
