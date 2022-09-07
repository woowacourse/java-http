package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;

public class HttpRequest {

    private final StartLine startLine;
    private final HttpRequestHeader requestHeader;
    private final HttpRequestBody requestBody;

    private HttpRequest(final StartLine startLine,
                        final HttpRequestHeader requestHeader,
                        final HttpRequestBody requestBody
    ) {
        this.startLine = startLine;
        this.requestHeader = requestHeader;
        this.requestBody = requestBody;
    }

    public static HttpRequest from(final BufferedReader bufferedReader) throws IOException {
        final StartLine startLine = StartLine.from(bufferedReader.readLine());
        final HttpRequestHeader header = HttpRequestHeader.from(bufferedReader);

        final int contentLength = Integer.parseInt(header.getContentLength());
        final HttpRequestBody body = HttpRequestBody.from(bufferedReader, contentLength);
        return new HttpRequest(startLine, header, body);
    }

    public boolean isGet() {
        return startLine.isGet();
    }

    public RequestURL getRequestURL() {
        return startLine.getRequestURL();
    }

    public String getHttpBody(String key) {
        return requestBody.get(key);
    }
}
