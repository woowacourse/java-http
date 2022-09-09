package org.apache.coyote.http11.request;

public class RequestLine {
    private final HttpMethod method;
    private final String path;
    private final HttpRequestParams params;

    private RequestLine(final HttpMethod method, final String path, final HttpRequestParams params) {
        this.method = method;
        this.path = path;
        this.params = params;
    }

    public static RequestLine from(final String startLine) {
        String[] splitLine = startLine.split(" ");
        String method = splitLine[0];
        String requestUri = splitLine[1];
        return new RequestLine(HttpMethod.from(method), parsePath(requestUri), HttpRequestParams.from(requestUri));
    }

    private static String parsePath(final String requestHeader) {
        return requestHeader.split(" ")[1]
                .split("\\?")[0];
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
