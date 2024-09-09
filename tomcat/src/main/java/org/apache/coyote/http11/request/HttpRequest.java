package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.coyote.http11.HttpCookie;

public class HttpRequest {
    private final RequestLine requestLine;
    private final Map<String, String> headers;
    private final String requestBody;

    private HttpRequest(final RequestLine requestLine, final Map<String, String> headers, final String requestBody) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.requestBody = requestBody;
    }

    public static HttpRequest from(final BufferedReader bufferedReader) throws IOException {
        final RequestLine requestLine = parseRequestLine(bufferedReader);
        final Map<String, String> headers = parseHeaders(bufferedReader);
        final String body = parseBody(bufferedReader, Integer.parseInt(headers.getOrDefault("Content-Length", "0")));
        return new HttpRequest(requestLine, headers, body);
    }

    private static RequestLine parseRequestLine(final BufferedReader bufferedReader) throws IOException {
        return RequestLine.of(bufferedReader.readLine());
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

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public String getRequestMethod() {
        return requestLine.getMethod();
    }

    public String getRequestPath() {
        return requestLine.getPath();
    }

    public Map<String, String> getHeaders() {
        return Collections.unmodifiableMap(headers);
    }

    public HttpCookie getCookies() {
        final String cookieString = headers.getOrDefault("Cookie", "");
        return new HttpCookie(cookieString);
    }

    public Map<String, String> parseRequestQuery() {
        final var params = requestBody.split("&");

        return Arrays.stream(params)
                .map(param -> param.split("="))
                .collect(Collectors.toMap(token -> token[0], token -> token[1]));
    }
}
