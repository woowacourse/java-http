package com.http.application;

import com.http.domain.HttpRequest;
import com.http.domain.StartLine;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class HttpRequestParser {

    private HttpRequestParser() {
    }

    public static HttpRequest parse(BufferedReader reader) throws IOException {
        final String requestLine = parseRequestLine(reader);
        final Map<String, String> headers = parseHeaders(reader);

        final StartLine startLine = StartLine.from(requestLine);
        final Map<String, String> queryStrings = parseQueryStrings(requestLine);

        return new HttpRequest(startLine, queryStrings, headers);
    }


    private static Map<String, String> parseQueryStrings(String requestLine) {
        if (requestLine == null || !requestLine.contains("?")) {
            return Map.of();
        }

        String queryString = requestLine.split(" ")[1].split("\\?")[1];
        String[] queries = queryString.split("&");

        return Stream.of(queries)
                .map(query -> query.split("="))
                .filter(query -> query.length == 2)
                .collect(Collectors.toMap(query -> query[0], query -> query[1]));
    }


    private static String parseRequestLine(BufferedReader reader) throws IOException {
        String requestLine = reader.readLine();
        if (requestLine == null || requestLine.trim().isEmpty()) {
            throw new IllegalArgumentException("Request line is null or empty");
        }
        return requestLine;
    }

    private static Map<String, String> parseHeaders(BufferedReader reader) throws IOException {
        Map<String, String> headers = new java.util.HashMap<>();
        String line;
        
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            String[] header = line.split(": ");
            if (header.length == 2) {
                headers.put(header[0], header[1]);
            }
        }
        
        return headers;
    }
}
