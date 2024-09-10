package org.apache.coyote.http11;

public class HttpRequest {

    private final RequestLine requestLine;
    private final RequestHeaders requestHeaders;
    private final String responseBody;

    public HttpRequest(RequestLine requestLine, RequestHeaders requestHeaders, String responseBody) {
        this.requestLine = requestLine;
        this.requestHeaders = requestHeaders;
        this.responseBody = responseBody;
    }

    public String getUrl() {
        return requestLine.getUrl();
    }

    public String getMethod() {
        return requestLine.getMethod();
    }

    public String getPath() {
        String url = requestLine.getUrl();
        return parsePath(url);
    }

    private String parsePath(String url) {
        int index = url.indexOf("?");
        if (index == -1) {
            return url;
        }
        return url.substring(0, index);
    }

    public QueryParam getQueryParam() {
        String method = requestLine.getMethod();
        if ("GET".equals(method)) {
            return new QueryParam(parseQueryString(requestLine.getUrl()));
        }
        return new QueryParam(responseBody);
    }

    private String parseQueryString(String uri) {
        int index = uri.indexOf("?");
        return uri.substring(index + 1);
    }

    public HttpCookies getCookies() {
        return requestHeaders.getCookies();
    }
}
