package org.apache.coyote.http11.request;

import org.apache.coyote.http11.common.HttpMethod;
import org.apache.coyote.http11.common.HttpProtocol;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpRequest {
    private final HttpRequestLine requestLine;
    private final HttpRequestHeaders headers;
    private final HttpRequestBody body;

    private HttpRequest(final HttpRequestLine requestLine, final HttpRequestHeaders headers, final HttpRequestBody body) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.body = body;
    }

    public static HttpRequest from(final BufferedReader bufferedReader) throws IOException {
        final List<String> requestHeader = extractRequestHeader(bufferedReader);
        final HttpRequestLine startLine = HttpRequestLine.from(requestHeader.get(0));
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
        final String contentLength = requestHeader.get("Content-Length");
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

    public HttpRequestLine getRequestLine() {
        return requestLine;
    }

    public Map<String, String> getBody() {
        return body.getBody();
    }

    public String getCookie() {
        return headers.get("Cookie");
    }

    public HttpMethod getMethod() {
        return requestLine.getMethod();
    }

    public String getPathValue() {
        return requestLine.getPathValue();
    }

    public HttpProtocol getProtocol() {
        return requestLine.getProtocol();
    }
}
