package org.apache.coyote.http11;

import java.util.Map;

public class Http11Request {

    private final RequestLine requestLine;
    private final Headers headers;
    private final Map<String, String> bodyParams;

    public Http11Request(final RequestLine requestLine, final Headers headers, final Map<String, String> bodyParams) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.bodyParams = bodyParams;
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

    public String getBodyParam(String paramName) {
        return bodyParams.getOrDefault(paramName, "");
    }
}
