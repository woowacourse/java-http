package org.apache.coyote.http11.request;

import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpMethod;

import java.io.BufferedReader;
import java.io.IOException;

public class HttpRequest {
    private final RequestLine requestLine;
    private final RequestHeaders requestHeaders;
    private final RequestBody requestBody;

    private HttpRequest(final RequestLine requestLine, final RequestHeaders requestHeaders, final RequestBody requestBody) {
        this.requestLine = requestLine;
        this.requestHeaders = requestHeaders;
        this.requestBody = requestBody;
    }

    public static HttpRequest from(final BufferedReader br) throws IOException {
        final RequestLine requestLine = RequestLine.from(br);
        final RequestHeaders requestHeaders = RequestHeaders.from(br);
        final RequestBody requestBody = RequestBody.of(requestHeaders.getContentLength(), br);

        return new HttpRequest(requestLine, requestHeaders, requestBody);
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public HttpMethod getMethod() {
        return requestLine.getMethod();
    }

    public String getAbsolutePath() {
        return requestLine.getAbsolutePath();
    }

    public String getExtension() {
        return requestLine.getExtension();
    }

    public RequestHeaders getRequestHeaders() {
        return requestHeaders;
    }

    public HttpCookie getCookies() {
        return requestHeaders.getCookie();
    }

    public RequestBody getRequestBody() {
        return requestBody;
    }
}
