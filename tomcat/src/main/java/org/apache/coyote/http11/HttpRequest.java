package org.apache.coyote.http11;

public class HttpRequest {

    private static final int DELIMITER_NOT_FOUND_CODE = -1;

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
        if (index == DELIMITER_NOT_FOUND_CODE) {
            return url;
        }
        return url.substring(0, index);
    }

    public Parameter getParameter() {
        String method = requestLine.getMethod();
        if ("GET".equals(method)) {
            return getParameterFromUrl(getUrl());
        }
        return getParameterFromBody(responseBody);
    }


    private Parameter getParameterFromUrl(String url) {
        QueryParamParser parser = new QueryParamParser(url);
        return parser.getParameter();
    }

    private Parameter getParameterFromBody(String responseBody) {
        FormUrlEncodedParser parser = new FormUrlEncodedParser(responseBody);
        return parser.getParameter();
    }

    public HttpCookies getCookies() {
        return requestHeaders.getCookies();
    }
}
