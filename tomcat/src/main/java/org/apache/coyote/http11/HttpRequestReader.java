package org.apache.coyote.http11;

import com.techcourse.exception.BadRequestException;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class HttpRequestReader {

    private static final String COLON = ":";
    private static final String QUESTION = "?";
    private static final int CR = '\r';
    private static final int LF = '\n';

    private final QueryParser queryParser;

    public HttpRequestReader(final QueryParser queryParser) {
        this.queryParser = queryParser;
    }

    public HttpRequest read(final InputStream inputStream) throws IOException {
        BufferedInputStream in = new BufferedInputStream(inputStream);

        RequestLine requestLine = readRequestLine(in);
        Map<String, String> headers = readHeaders(in);

        String uri = requestLine.uri();
        String path = findPath(uri);
        Map<String, String> queries = parseQueryString(uri);

        int contentLength = Integer.parseInt(headers.getOrDefault("content-length", "0"));
        byte[] body = in.readNBytes(contentLength);

        return new HttpRequest(requestLine, headers, path, queries, body);
    }

    private Map<String, String> parseQueryString(final String uri) {
        if (!uri.contains(QUESTION)) {
            return Map.of();
        }
        final String queryString = extractQueryString(uri);

        return queryParser.parse(queryString);
    }

    private RequestLine readRequestLine(final BufferedInputStream in) throws IOException {
        String requestLine = readLine(in);
        if (requestLine.isEmpty()) {
            throw new BadRequestException("Request line is empty");
        }

        String[] parts = requestLine.split(" ");
        HttpMethod method = HttpMethod.from(parts[0]);
        String uri = parts[1];
        HttpVersion version = HttpVersion.from(parts[2]);

        return new RequestLine(method, uri, version);
    }

    private Map<String, String> readHeaders(final BufferedInputStream in) throws IOException {
        Map<String, String> headers = new HashMap<>();
        String line;
        while ((line = readLine(in)) != null && !line.isEmpty()) {
            int colon = line.indexOf(COLON);
            if (colon < 0) {
                throw new BadRequestException("Header is malformed");
            }
            String name = line.substring(0, colon).toLowerCase().trim();
            String value = line.substring(colon + 1).trim();

            if (!headers.containsKey(name)) {
                headers.put(name, value);
            }
        }

        return headers;
    }

    private String readLine(BufferedInputStream in) throws IOException {
        StringBuilder sb = new StringBuilder();
        int prev = -1, cur;
        while ((cur = in.read()) != -1) {
            if (prev == CR && cur == LF) {
                break;
            }
            if (prev != -1) {
                sb.append((char) prev);
            }
            prev = cur;
        }
        return sb.toString();
    }

    private String findPath(final String uri) {
        int queryStartIndex = uri.indexOf(QUESTION);
        if (queryStartIndex < 0) {
            return uri;
        }

        return uri.substring(0, queryStartIndex);
    }

    private String extractQueryString(final String uri) {
        int questionIndex = uri.indexOf(QUESTION);
        if (questionIndex < 0) {
            return uri;
        }

        return uri.substring(questionIndex + 1);
    }
}
