package org.apache.coyote.http11;

public class Http11Request {

    private final RequestLine requestLine;
    private final Headers headers;
    private final Cookies cookies;
    private final RequestBodyParams requestBodyParams;

    public Http11Request(
            final RequestLine requestLine,
            final Headers headers,
            final RequestBodyParams requestBodyParams
    ) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.cookies = new Cookies();
        this.cookies.parseCookies(headers.getHeader("cookie"));
        this.requestBodyParams = requestBodyParams;
    }

    public String getMethod() {
        return requestLine.getMethod();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public String getQueryParam(final String queryParamName) {
        return requestLine.getQueryParam(queryParamName);
    }

    public String getProtocol() {
        return requestLine.getProtocol();
    }

    public String getHeader(String headerName) {
        return headers.getHeader(headerName);
    }

    public String getBodyParam(String bodyParamName) {
        return requestBodyParams.getBodyParam(bodyParamName);
    }

    public String getCookie(String cookieName) {
        return cookies.getCookie(cookieName);
    }
}
