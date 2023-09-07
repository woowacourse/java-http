package org.apache.common;

import java.util.List;

public class HttpRequest {

    private final HttpLine httpLine;
    private final HttpHeader httpHeader;
    private final String requestBody;

    public HttpRequest(HttpLine httpLine, HttpHeader httpHeader, String requestBody) {
        this.httpLine = httpLine;
        this.httpHeader = httpHeader;
        this.requestBody = requestBody;
    }

    public static HttpRequest of(String httpLine, List<String> headers, String requestBody) {
        return new HttpRequest(HttpLine.from(httpLine), HttpHeader.from(headers), requestBody);
    }

    public HttpLine getHttpLine() {
        return httpLine;
    }

    public HttpHeader getHttpHeader() {
        return httpHeader;
    }

    public String getRequestBody() {
        return requestBody;
    }
}
