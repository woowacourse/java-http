package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.coyote.http11.common.HttpHeaders;

public class HttpRequest {

    private static final int HTTP_REQUEST_HEADER_START_INDEX = 0;

    private final RequestLine requestLine;
    private final HttpHeaders headers;
    private final String requestBody;

    public HttpRequest(final RequestLine requestLine,
                       final HttpHeaders headers,
                       final String requestBody) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.requestBody = requestBody;
    }

    public static HttpRequest parse(final BufferedReader reader) throws IOException {
        final RequestLine requestLine = RequestLine.parse(reader.readLine());
        if (requestLine.getMethod().equals("GET")) {
            final List<String> lines = readAllLines(reader);
            final HttpHeaders headers = HttpHeaders.parse(readHeaders(lines));
            final String requestBody = findRequestBody(reader, headers);

            return new HttpRequest(requestLine, headers, requestBody);
        }

        final List<String> lines = readAllLines(reader);
        final HttpHeaders headers = HttpHeaders.parse(readHeaders(lines));
        final String requestBody = findRequestBody(reader, headers);

        return new HttpRequest(requestLine, headers, requestBody);
    }

    private static List<String> readAllLines(final BufferedReader reader) throws IOException {
        final List<String> lines = new ArrayList<>();
        while (reader.ready()) {
            final String line = reader.readLine();
            lines.add(line);
        }
        return lines;
    }

    private static List<String> readHeaders(final List<String> lines) {
        final List<String> headers = new ArrayList<>();

        for (final String line : lines) {
            if (line.isEmpty()) {
                break;
            }
            headers.add(line);
        }

        return headers;
    }

    private static String findRequestBody(final BufferedReader reader, final HttpHeaders headers)
            throws IOException {
        if (headers.hasContentLength()) {
            return reader.readLine();
        }
        return "";
    }

    public String getUri() {
        return requestLine.getUri();
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public String getRequestBody() {
        return requestBody;
    }
}
