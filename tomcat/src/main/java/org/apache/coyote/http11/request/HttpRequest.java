package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HttpRequest {
    private final HttpRequestStartLine startLine;
    private final HttpRequestHeader header;
    private final HttpRequestBody body;

    private HttpRequest(final HttpRequestStartLine startLine, final HttpRequestHeader header, final HttpRequestBody body) {
        this.startLine = startLine;
        this.header = header;
        this.body = body;
    }

    public static HttpRequest from(final BufferedReader bufferedReader) throws IOException {
        final List<String> request = new ArrayList<>();

        String line = bufferedReader.readLine();
        while (!"".equals(line) && line != null) {
            request.add(line);
            line = bufferedReader.readLine();
        }

        final HttpRequestStartLine startLine = HttpRequestStartLine.from(request.get(0));
        final HttpRequestHeader requestHeader = HttpRequestHeader.from(request.subList(1, request.size()));
        HttpRequestBody requestBody;
        final String contentLength = requestHeader.getContentLength();

        if (contentLength == null) {
            requestBody = HttpRequestBody.empty();
            return new HttpRequest(startLine, requestHeader, requestBody);
        }

        final int length = Integer.parseInt(contentLength);
        final char[] buffer = new char[length];
        bufferedReader.read(buffer, 0, length);
        requestBody = HttpRequestBody.from(new String(buffer));
        return new HttpRequest(startLine, requestHeader, requestBody);
    }

    public boolean hasCookie() {
        return header.hasCookie();
    }

    public HttpRequestStartLine getStartLine() {
        return startLine;
    }

    public HttpRequestHeader getHeader() {
        return header;
    }

    public String getBody() {
        return body.getHttpRequestBodys();
    }

    public String getCookie() {
        return header.getCookie();
    }
}
