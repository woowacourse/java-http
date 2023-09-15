package org.apache.coyote.request;

import java.io.BufferedReader;
import java.io.IOException;
import org.apache.coyote.common.HttpMethod;
import org.apache.coyote.common.HttpVersion;
import org.apache.coyote.common.HttpCookies;

public class HttpRequest {

    private final RequestLine requestLine;
    private final RequestHeaders requestHeaders;
    private final HttpCookies cookies;
    private final RequestBody requestBody;

    private HttpRequest(final RequestLine requestLine, final RequestHeaders requestHeaders,
                        final HttpCookies cookies, final RequestBody requestBody) {
        this.requestLine = requestLine;
        this.requestHeaders = requestHeaders;
        this.cookies = cookies;
        this.requestBody = requestBody;
    }

    public static HttpRequest from(final BufferedReader reader) throws IOException {
        final RequestLine requestLine = RequestLine.from(reader.readLine());
        final RequestHeaders requestHeader = RequestHeaders.from(reader);
        final HttpCookies cookies = HttpCookies.from(requestHeader.getHeaderValue("Cookie"));
        final RequestBody requestBody = readRequestBody(reader, requestHeader);

        return new HttpRequest(requestLine, requestHeader, cookies, requestBody);
    }

    private static RequestBody readRequestBody(final BufferedReader reader, final RequestHeaders headers)
            throws IOException {
        final String contentLength = headers.getHeaderValue("Content-Length");
        if (contentLength != null) {
            return RequestBody.of(reader, contentLength);
        }
        return null;
    }

    public HttpMethod getHttpMethod() {
        return requestLine.getHttpMethod();
    }

    public String getRequestUri() {
        return requestLine.getRequestUri();
    }

    public HttpVersion getHttpVersion() {
        return requestLine.getHttpVersion();
    }

    public String getHeaderValue(final String header) {
        return requestHeaders.getHeaderValue(header);
    }

    public HttpCookies getCookies() {
        return cookies;
    }

    public String getRequestBody() {
        return requestBody.getContent();
    }
}
