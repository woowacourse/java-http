package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.catalina.domain.HttpHeader;
import org.apache.catalina.domain.HttpRequest;
import org.apache.catalina.domain.RequestStartLine;

public final class HttpRequestParser {

    private HttpRequestParser() {
    }

    public static HttpRequest parse(BufferedReader reader) throws IOException {
        final List<String> requestLines = parseRequestLines(reader);

        final RequestStartLine requestStartLine = RequestStartLine.from(requestLines);
        final Map<String, String> queryStrings = parseQueryStrings(requestLines);
        final HttpHeader httpHeader = HttpHeader.from(requestLines);

        return new HttpRequest(requestStartLine, queryStrings, httpHeader);
    }

    private static List<String> parseRequestLines(BufferedReader reader) throws IOException {
        List<String> requestLines = new ArrayList<>();
        String line;

        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            requestLines.add(line);
        }

        return requestLines;
    }

    // TODO ?에 대한 파싱 처리 필요할 거 같음
    private static Map<String, String> parseQueryStrings(List<String> requestLines) {
        final String startLine = requestLines.getFirst();

        if (startLine == null || !startLine.contains("?")) {
            return Map.of();
        }

        String queryString = startLine.split(" ")[1].split("\\?")[1];
        String[] queries = queryString.split("&");

        return Stream.of(queries)
                .map(query -> query.split("="))
                .filter(query -> query.length == 2)
                .collect(Collectors.toMap(query -> query[0], query -> query[1]));
    }
    }
