package org.apache.coyote.http11;

import java.io.BufferedReader;

public class HttpRequest {

    private final HttpRequestHeader httpRequestHeader;
    private HttpRequestBody httpRequestBody;

    public HttpRequest(BufferedReader bufferedReader) {
        this.httpRequestHeader = new HttpRequestHeader(bufferedReader);
        if (httpRequestHeader.containsKey("Content-Length")) {
            int contentLength = Integer.parseInt(httpRequestHeader.getValue("Content-Length"));
            this.httpRequestBody = new HttpRequestBody(contentLength, bufferedReader);
        }
    }

    public HttpRequestHeader getHttpRequestHeader() {
        return httpRequestHeader;
    }

    public HttpRequestBody getHttpRequestBody() {
        return httpRequestBody;
    }

    @Override
    public String toString() {
        return "HttpRequest{\n" +
                "httpRequestHeader=" + httpRequestHeader +
                ",\n httpRequestBody=" + httpRequestBody +
                "\n}";
    }
}
