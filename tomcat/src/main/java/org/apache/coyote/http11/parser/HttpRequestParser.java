package org.apache.coyote.http11.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.coyote.http11.dto.HttpRequest;

public final class HttpRequestParser {

    private static final String QUESTION = "?";
    private static final String EMPTY = "";
    private static final String COLON = ":";
    private static final String REQUEST_LINE_DELIMITER = " ";

    private HttpRequestParser() {
    }

    public static Optional<HttpRequest> parse(final InputStream inputStream) throws IOException {

        final InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        final String requestLine = bufferedReader.readLine();
        if (requestLine == null || requestLine.isBlank()) {
            return Optional.empty();
        }

        // 요청 라인(Request Line) 파싱
        final String[] parts = requestLine.split(REQUEST_LINE_DELIMITER);
        final String method = parts[0].trim();
        final String uri = parts[1].trim();
        final String version = parts[2].trim();

        // 쿼리 파싱
        final int qIdx = uri.indexOf(QUESTION);
        final String route = (qIdx >= 0) ? uri.substring(0, qIdx) : uri;
        final String queryString = (qIdx >= 0) ? uri.substring(qIdx + 1) : EMPTY;
        final Map<String, String> query = QueryStringParser.parse(queryString);

        // 헤더 파싱
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

        return Optional.of(new HttpRequest(method, route, query, version, headers));
    }
}
