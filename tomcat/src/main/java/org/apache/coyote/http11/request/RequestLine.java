package org.apache.coyote.http11.request;

public class RequestLine {
    private static final String LINE_SEPARATOR = " ";
    private static final int METHOD_INDEX = 0;
    private static final int FIRST_INDEX = METHOD_INDEX;
    private static final int SECOND_INDEX = 1;
    private final HttpMethod method;
    private final String path;
    private final HttpRequestParams params;

    private RequestLine(final HttpMethod method, final String path, final HttpRequestParams params) {
        this.method = method;
        this.path = path;
        this.params = params;
    }

    public static RequestLine from(final String startLine) {
        String[] splitLine = startLine.split(LINE_SEPARATOR);
        String method = splitLine[FIRST_INDEX];
        String requestUri = splitLine[SECOND_INDEX];
        return new RequestLine(HttpMethod.from(method), parsePath(requestUri), HttpRequestParams.from(requestUri));
    }

    private static String parsePath(final String requestUri) {
        return requestUri.split("\\?")[FIRST_INDEX];
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public HttpRequestParams getParams() {
        return params;
    }

    public boolean isMethodGet() {
        return method.equals(HttpMethod.GET);
    }

    public boolean isMethodPost() {
        return method.equals(HttpMethod.POST);
    }
}
