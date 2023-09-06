package org.apache.coyote.http11.domain;

import org.apache.coyote.domain.HttpRequestHeader;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpRequest {

    private static final String HEADER_DELIMITER = ": ";

    private final HttpRequestHeader requestHeader;
    private final HttpRequestBody requestBody;

    private HttpRequest(final HttpRequestHeader requestHeader, final HttpRequestBody requestBody) {
        this.requestHeader = requestHeader;
        this.requestBody = requestBody;
    }

    public static HttpRequest from(final BufferedReader bufferedReader) throws IOException {
        final List<String> httpRequestHeaders = parseHttpRequestHeaders(bufferedReader);
        final HttpRequestHeader httpRequestHeader = parseHttpRequestHeader(httpRequestHeaders);
        final String contentLength = httpRequestHeader.getHeaders().get("Content-Length");
        if (contentLength == null) {
            return new HttpRequest(httpRequestHeader, null);
        }
        final HttpRequestBody httpRequestBody = parseHttpRequestBody(bufferedReader, Integer.parseInt(contentLength));
        return new HttpRequest(httpRequestHeader, httpRequestBody);
    }

    private static List<String> parseHttpRequestHeaders(final BufferedReader bufferedReader) throws IOException {
        final List<String> lines = new ArrayList<>();
        String line = bufferedReader.readLine();
        while (!line.isEmpty()) {
            lines.add(line);
            line = bufferedReader.readLine();
        }
        return lines;
    }

    private static HttpRequestHeader parseHttpRequestHeader(final List<String> httpRequestHeader) {
        final String[] firstHeader = httpRequestHeader.remove(0).split(" ");
        final String method = firstHeader[0];
        final String uri = firstHeader[1];
        final String version = firstHeader[2];
        final Map<String, String> requestHeaders = new HashMap<>();
        for (final String header : httpRequestHeader) {
            final String[] splitHeader = header.split(HEADER_DELIMITER);
            requestHeaders.put(splitHeader[0], splitHeader[1]);
        }
        return new HttpRequestHeader(method, uri, version, requestHeaders);
    }

    private static HttpRequestBody parseHttpRequestBody(final BufferedReader bufferedReader, final int contentLength) throws IOException {
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        final String body = new String(buffer);
        return HttpRequestBody.from(body);
    }

    public String getUri() {
        return requestHeader.getUri();
    }

    public HttpRequestBody getBody() {
        return requestBody;
    }

    public String getMethod() {
        return requestHeader.getMethod();
    }
}
