package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;

public class HttpRequest {
    private final RequestLine requestLine;
    private final RequestHeader requestHeader;
    private RequestBody requestBody;


    public HttpRequest(final RequestLine requestLine, final RequestHeader requestHeader) {
        this.requestLine = requestLine;
        this.requestHeader = requestHeader;
    }

    public static HttpRequest from(final BufferedReader bufferedReader) {
        try {
            final RequestLine requestLine = RequestLine.of(bufferedReader.readLine());
            final RequestHeader requestHeader = RequestHeader.of(bufferedReader);
            return new HttpRequest(requestLine, requestHeader);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String parseUriPath() {
        return requestLine.getRequestUri().getPath();
    }

    public String getParameter(final String key) {
        return requestLine.getRequestUri().getParamByName(key);
    }

    public String getHeaderField(final String fieldName) {
        return requestHeader.getField(fieldName);
    }
}
