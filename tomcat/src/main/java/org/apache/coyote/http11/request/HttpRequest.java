package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.coyote.http11.utils.QueryParamsParser;

public class HttpRequest {

    private static final int REQUEST_START_INDEX = 0;

    private final HttpRequestLine line;
    private final HttpRequestHeaders headers;
    private final RequestBody requestBody;

    public HttpRequest(final HttpRequestLine line, final HttpRequestHeaders headers,
                       final RequestBody requestBody) {
        this.line = line;
        this.headers = headers;
        this.requestBody = requestBody;
    }

    public static HttpRequest parse(final BufferedReader reader) {
        final List<String> lines = readAllLines(reader);
        int emptyLineIndex = getEmptyLineIndex(lines);
        return new HttpRequest(
                HttpRequestLine.parse(lines.get(REQUEST_START_INDEX)),
                HttpRequestHeaders.parse(lines.subList(REQUEST_START_INDEX + 1, emptyLineIndex)),
                RequestBody.parse(lines.subList(Math.min(emptyLineIndex + 1, lines.size()), lines.size()))
        );
    }

    private static List<String> readAllLines(final BufferedReader reader) {
        final List<String> lines = new ArrayList<>();

        String line;
        while (!(line = readOneLine(reader)).equals("")) {
            lines.add(line);
        }
        return lines;
    }

    private static String readOneLine(final BufferedReader reader) {
        try {
            return reader.readLine();
        } catch (Exception e) {
            throw new IllegalArgumentException();
        }
    }

    private static int getEmptyLineIndex(final List<String> lines) {
        final int emptyLineIndex = lines.indexOf("");
        if (emptyLineIndex == -1) {
            return lines.size();
        }
        return emptyLineIndex;
    }

    public HttpMethod getHttpMethod() {
        return line.getHttpMethod();
    }

    public String getMethod() {
        return line.getMethod();
    }

    public String getRequestUrl() {
        return line.getRequestUrl();
    }

    public Map<String, String> queryParamsData() {
        return QueryParamsParser.parse(line.getRequestUrl());
    }

    public HttpRequestLine getLine() {
        return line;
    }

    public HttpRequestHeaders getHeaders() {
        return headers;
    }

    public RequestBody getRequestBody() {
        return requestBody;
    }
}
