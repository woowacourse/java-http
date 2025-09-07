package org.apache.coyote.http11.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.coyote.http11.dto.HttpRequest;
import org.apache.coyote.http11.dto.RequestLine;

public final class HttpRequestParser {

    private static final String CONTENT_LENGTH_HEADER = "Content-Length";
    private static final String QUESTION = "?";
    private static final String EMPTY = "";
    private static final String COLON = ":";
    private static final String REQUEST_LINE_DELIMITER = " ";

    private HttpRequestParser() {
    }

    public static Optional<HttpRequest> parse(final InputStream inputStream) throws IOException {
        final InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        // 1. 요청 라인(Request Line) 파싱
        final RequestLine requestLine = parseRequestLine(bufferedReader);

        // 2. 헤더 파싱
        final Map<String, String> headers = parseHeaders(bufferedReader);

        // 3. URI 파싱 (경로, 쿼리)
        final String route = extractRoute(requestLine.uri());
        final Map<String, String> query = extractQueryFromUri(requestLine.uri());

        // 4. 바디 파싱 및 쿼리 파리미터에 포함
        final String body = parseBody(bufferedReader, headers);
        if (!body.isEmpty()) {
            final Map<String, String> bodyQuery = QueryStringParser.parse(body);
            query.putAll(bodyQuery);
        }

        return Optional.of(new HttpRequest(requestLine.method(), route, query, requestLine.protocol(), headers));
    }

    // 1. 요청 라인(Request Line) 파싱
    private static RequestLine parseRequestLine(final BufferedReader bufferedReader) throws IOException {
        final String line = bufferedReader.readLine();
        if (line == null || line.isBlank()) {
            throw new IOException("Request line is empty");
        }

        final String[] parts = line.split(REQUEST_LINE_DELIMITER);
        return new RequestLine(parts[0].trim(), parts[1].trim(), parts[2].trim());
    }

    // 2. 헤더 파싱
    private static Map<String, String> parseHeaders(final BufferedReader bufferedReader) throws IOException {
        final Map<String, String> headers = new LinkedHashMap<>();
        String line;
        while ((line = bufferedReader.readLine()) != null && !line.isEmpty()) {
            final int idx = line.indexOf(COLON);
            if (idx > 0) {
                final String name = line.substring(0, idx).trim();
                final String value = line.substring(idx + 1).trim();
                headers.put(name, value);
            }
        }
        return headers;
    }

    // 3. URI 파싱 (경로)
    private static String extractRoute(final String uri) {
        final int qIdx = uri.indexOf(QUESTION);
        return (qIdx >= 0) ? uri.substring(0, qIdx) : uri;
    }

    // 3. URI 파싱 (쿼리)
    private static Map<String, String> extractQueryFromUri(final String uri) {
        final int qIdx = uri.indexOf(QUESTION);
        if (qIdx < 0) {
            return new HashMap<>();
        }
        final String queryString = uri.substring(qIdx + 1);
        return QueryStringParser.parse(queryString);
    }

    // 4. 바디 파싱 및 쿼리 파리미터에 포함
    private static String parseBody(final BufferedReader bufferedReader, final Map<String, String> headers)
            throws IOException {
        final int contentLength = Integer.parseInt(headers.getOrDefault(CONTENT_LENGTH_HEADER, "0"));
        if (contentLength == 0) {
            return EMPTY;
        }

        final char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        return new String(buffer);
    }
}
