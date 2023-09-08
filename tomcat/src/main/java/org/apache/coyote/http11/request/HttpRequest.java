package org.apache.coyote.http11.request;

import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpMethod;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

public class HttpRequest {
    private final RequestLine requestLine;
    private final RequestHeaders requestHeaders;
    private final RequestBody requestBody;
    private static final String CONTENT_LENGTH = "Content-Length";

    private HttpRequest(final RequestLine requestLine, final RequestHeaders requestHeaders, final RequestBody requestBody) {
        this.requestLine = requestLine;
        this.requestHeaders = requestHeaders;
        this.requestBody = requestBody;
    }

    public static HttpRequest from(final BufferedReader br) throws IOException {
        final RequestLine requestLine = RequestLine.from(br);
        final RequestHeaders requestHeaders = RequestHeaders.from(br);
        final RequestBody requestBody = RequestBody.of(requestHeaders.getValue(CONTENT_LENGTH), br);

        return new HttpRequest(requestLine, requestHeaders, requestBody);
    }

    public RequestHeaders getRequestHeaders() {
        return requestHeaders;
    }

    public HttpCookie getCookies() {
        return requestHeaders.getCookie();
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

    public String getRequestBody() {
        return requestBody.getRequestBody();
    }

    public Map<String, String> getRequestParam() {
        return requestLine.getRequestParam();
    }
}
