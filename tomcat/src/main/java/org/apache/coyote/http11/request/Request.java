package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;

public class Request {
    private final RequestLine requestLine;
    private final RequestHeader requestHeader;
    private final RequestBody requestBody;

    private Request(final RequestLine requestLine,
                    final RequestHeader requestHeader,
                    final RequestBody requestBody) {
        this.requestLine = requestLine;
        this.requestHeader = requestHeader;
        this.requestBody = requestBody;
    }

    public static Request from(final BufferedReader bufferedReader) throws IOException {
        final RequestLine requestLine = RequestLine.from(bufferedReader.readLine());
        final RequestHeader requestHeader = RequestHeader.from(bufferedReader);
        final RequestBody requestBody = RequestBody.of(requestHeader, bufferedReader);
        return new Request(requestLine, requestHeader, requestBody);
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public RequestHeader getRequestHeader() {
        return requestHeader;
    }

    public RequestBody getRequestBody() {
        return requestBody;
    }

    public String getAccessType() {
        return requestHeader.getHeaderValue("AccessType");
    }
}
