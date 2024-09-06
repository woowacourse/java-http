package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpRequest {
    private final String requestLine;
    private final Map<String, String> headers;
    private final String requestBody;

    private HttpRequest(final String requestLine, final Map<String, String> headers, final String requestBody) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.requestBody = requestBody;
    }

    public static HttpRequest from(final BufferedReader bufferedReader) throws IOException {
        final String requestLine = parseRequestLine(bufferedReader);
        final Map<String, String> headers = parseHeaders(bufferedReader);
        final String body = parseBody(bufferedReader, Integer.parseInt(headers.getOrDefault("Content-Length", "0")));
        return new HttpRequest(requestLine, headers, body);
    }

    private static String parseRequestLine(final BufferedReader bufferedReader) throws IOException {
        return bufferedReader.readLine();
    }

    private static Map<String, String> parseHeaders(final BufferedReader reader) throws IOException {
        final Map<String, String> headers = new HashMap<>();
        var header = reader.readLine();

        while (!"".equals(header)) {
            final var tokens = header.split(": ");
            headers.put(tokens[0], tokens[1]);
            header = reader.readLine();
        }

        return headers;
    }

    private static String parseBody(final BufferedReader reader, final int contentLength) throws IOException {
        char[] buffer = new char[contentLength];
        reader.read(buffer, 0, contentLength);
        return new String(buffer);
    }

    public String getRequestLine() {
        return requestLine;
    }

    public String getRequestMethod() {
        return requestLine.split(" ")[0];
    }

    public String getRequestPath() {
        return requestLine.split(" ")[1];
    }

    public Map<String, String> getHeaders() {
        return Collections.unmodifiableMap(headers);
    }

    public Map<String, String> getCookies() {
        final String cookieString = headers.getOrDefault("Cookie", "");
        final var params = cookieString.split("; ");

        return Arrays.stream(params)
                .map(param -> param.split("="))
                .collect(Collectors.toMap(token -> token[0], token -> token[1]));
    }

    public Map<String, String> parseRequestQuery() throws IOException {
        final var params = requestBody.split("&");

        return Arrays.stream(params)
                .map(param -> param.split("="))
                .collect(Collectors.toMap(token -> token[0], token -> token[1]));
    }
}
