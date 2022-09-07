package org.apache.coyote.http11.model.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.coyote.http11.model.HttpHeaderType;
import org.apache.coyote.http11.model.HttpMethod;

public class HttpRequest {

    private static final String EMPTY_LINE = "";

    private final HttpRequestLine requestLine;
    private final HttpRequestHeader requestHeader;
    private final HttpRequestBody requestBody;

    private HttpRequest(HttpRequestLine requestLine, HttpRequestHeader requestHeader, HttpRequestBody requestBody) {
        this.requestLine = requestLine;
        this.requestHeader = requestHeader;
        this.requestBody = requestBody;
    }

    public static HttpRequest from(BufferedReader reader) throws IOException {
        HttpRequestLine requestLine = new HttpRequestLine(reader.readLine());
        HttpRequestHeader header = createRequestHeader(reader);
        HttpRequestBody body = createRequestBody(reader, header);

        return new HttpRequest(requestLine, header, body);
    }

    private static HttpRequestBody createRequestBody(BufferedReader reader, HttpRequestHeader header)
            throws IOException {
        if (header.hasNotHeader(HttpHeaderType.CONTENT_LENGTH)) {
            return new HttpRequestBody(null);
        }
        int contentLength = Integer.parseInt(header.getHeaderValue(HttpHeaderType.CONTENT_LENGTH));
        char[] buffer = new char[contentLength];
        reader.read(buffer, 0, contentLength);
        String requestBody = new String(buffer);
        return new HttpRequestBody(requestBody);
    }

    private static HttpRequestHeader createRequestHeader(BufferedReader reader) {
        List<String> requestHeader = reader.lines()
                .takeWhile(line -> !EMPTY_LINE.equals(line))
                .collect(Collectors.toList());
        return HttpRequestHeader.from(requestHeader);
    }

    public HttpMethod getMethod() {
        return requestLine.getMethod();
    }

    public String getUri() {
        return requestLine.getUri();
    }

    public String getProtocolVersion() {
        return requestLine.getProtocolVersion();
    }

    public String getHeaderValue(String key) {
        return requestHeader.getHeaderValue(key);
    }

    public String getBodyValue(String key) {
        return requestBody.getBodyValue(key);
    }

    public String getCookieValue(String key) {
        return requestHeader.getCookieValue(key);
    }

    public boolean hasSession() {
        return requestHeader.hasCookie();
    }
}
