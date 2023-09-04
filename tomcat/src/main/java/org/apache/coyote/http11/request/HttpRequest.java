package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.coyote.http11.common.HttpHeaders;

public class HttpRequest {

    private static final int HTTP_REQUEST_LINE_INDEX = 0;
    private static final int HTTP_REQUEST_HEADER_START_INDEX = 1;

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
        final List<String> lines = readAllLines(reader);

        final RequestLine requestLine = RequestLine.parse(lines.get(HTTP_REQUEST_LINE_INDEX));
        final HttpHeaders headers = HttpHeaders.parse(readHeaders(lines));
        final String requestBody = findRequestBody(reader, headers);

        return new HttpRequest(requestLine, headers, requestBody);
    }

    private static List<String> readAllLines(final BufferedReader reader) throws IOException {
        final List<String> lines = new ArrayList<>();
        while (reader.ready()) {
            lines.add(reader.readLine());
        }
        return lines;
    }

    private static List<String> readHeaders(final List<String> lines) {
        final int linesSize = lines.size();
        final List<String> headerLines = lines.subList(HTTP_REQUEST_HEADER_START_INDEX, linesSize);
        final List<String> headers = new ArrayList<>();

        for (final String line : headerLines) {
            if (line.isEmpty()) {
                continue;
            }
            headers.add(line);
        }

        return headers;
    }

    private static String findRequestBody(final BufferedReader reader, final HttpHeaders headers)
            throws IOException {
        if (headers.hasContentLength()) {
            final int contentLength = headers.getContentLength();
            final char[] buffer = new char[contentLength];
            reader.read(buffer, 0, contentLength);
            return new String(buffer);
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
