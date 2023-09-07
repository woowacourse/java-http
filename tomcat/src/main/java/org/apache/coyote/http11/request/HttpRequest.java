package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HttpRequest {
    private final HttpRequestStartLine startLine;
    private final HttpRequestHeaders headers;
    private final HttpRequestBody body;

    private HttpRequest(final HttpRequestStartLine startLine, final HttpRequestHeaders headers, final HttpRequestBody body) {
        this.startLine = startLine;
        this.headers = headers;
        this.body = body;
    }

    public static HttpRequest from(final BufferedReader bufferedReader) throws IOException {
        final List<String> requestHeader = extractRequestHeader(bufferedReader);
        final HttpRequestStartLine startLine = HttpRequestStartLine.from(requestHeader.get(0));
        final HttpRequestHeaders headers = HttpRequestHeaders.from(requestHeader.subList(1, requestHeader.size()));
        final HttpRequestBody requestBody = extractRequestBody(bufferedReader, headers);
        return new HttpRequest(startLine, headers, requestBody);
    }

    private static List<String> extractRequestHeader(final BufferedReader bufferedReader) throws IOException {
        final List<String> requestHeader = new ArrayList<>();
        String line = bufferedReader.readLine();
        while (!"".equals(line) && line != null) {
            requestHeader.add(line);
            line = bufferedReader.readLine();
        }
        return requestHeader;
    }

    private static HttpRequestBody extractRequestBody(final BufferedReader bufferedReader, final HttpRequestHeaders requestHeader) throws IOException {
        final String contentLength = requestHeader.getContentLength();
        if (contentLength == null) {
            return HttpRequestBody.empty();
        }
        final int length = Integer.parseInt(contentLength);
        final char[] buffer = new char[length];
        bufferedReader.read(buffer, 0, length);
        return HttpRequestBody.from(new String(buffer));
    }

    public boolean hasCookie() {
        return headers.hasCookie();
    }

    public HttpRequestStartLine getStartLine() {
        return startLine;
    }

    public HttpRequestHeaders getHeaders() {
        return headers;
    }

    public String getBody() {
        return body.getBody();
    }

    public String getCookie() {
        return headers.getCookie();
    }
}
