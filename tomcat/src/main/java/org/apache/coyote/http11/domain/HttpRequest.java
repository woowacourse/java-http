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
    private final Map<String, String> requestBody;

    private HttpRequest(final HttpRequestHeader requestHeader, final Map<String, String> requestBody) {
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
        final Map<String, String> requestBody = parseHttpRequestBody(bufferedReader, Integer.parseInt(contentLength));
        return new HttpRequest(httpRequestHeader, requestBody);
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

    private static Map<String, String> parseHttpRequestBody(final BufferedReader bufferedReader, final int contentLength) throws IOException {
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        final String body = new String(buffer);
        return splitBody(body);
    }

    private static Map<String, String> splitBody(final String body) {
        final Map<String, String> requestBody = new HashMap<>();
        final String[] splitBodies = body.split("&");
        for (final String splitBody : splitBodies) {
            final String[] keyAndValue = splitBody.split("=");
            requestBody.put(keyAndValue[0], keyAndValue[1]);
        }

        return requestBody;
    }

    public String getUri() {
        return requestHeader.getUri();
    }

    public Map<String, String> getBody() {
        return requestBody;
    }

    public String getMethod() {
        return requestHeader.getMethod();
    }
}
