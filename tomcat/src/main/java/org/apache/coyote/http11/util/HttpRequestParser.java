package org.apache.coyote.http11.util;

import org.apache.catalina.vo.HttpRequest;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpRequestParser {

    public static HttpRequest parse(InputStream inputStream) throws IOException {
        final var reader = new BufferedReader(new InputStreamReader(inputStream));
        final var requestLine = reader.readLine();

        final var requestLineElements = Arrays.stream(requestLine.split("\\s+")).toList();
        if (requestLineElements.size() != 3) {
            throw new IllegalArgumentException();
        }

        final Map<String, String> headers = getHeaders(reader);

        final var body = getBody(headers, reader);
        return new HttpRequest(
                requestLineElements.getFirst(),
                requestLineElements.get(1),
                requestLineElements.getLast(),
                headers,
                body
        );
    }

    private static Map<String, String> getHeaders(final BufferedReader reader) throws IOException {
        final List<String> headerLines = getHeaderLines(reader);

        final var result = new HashMap<String, String>();
        for (String line : headerLines) {
            final var colonIndex = line.indexOf(":");
            final var key = line.substring(0, colonIndex).trim();
            final var value = line.substring(colonIndex + 1).trim();
            result.put(key, value);
        }
        return result;
    }

    private static List<String> getHeaderLines(final BufferedReader reader) throws IOException {
        final var headerLines = new ArrayList<String>();
        String line;
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            headerLines.add(line);
        }
        return headerLines;
    }

    private static String getBody(Map<String, String> headers, BufferedReader reader) throws IOException {
        int contentLength;
        try {
            contentLength = Integer.parseInt(headers.getOrDefault("Content-Length", "0"));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException();
        }

        char[] buffer = new char[contentLength];
        if (reader.read(buffer, 0, contentLength) == -1) {
            throw new EOFException();
        }
        return new String(buffer);
    }

    public static Map<String, String> parseQueryString(final String queryString) {
        if (queryString.isEmpty()) {
            return Map.of();
        }

        final Map<String, String> result = new HashMap<>();
        final var split = Arrays.stream(queryString.split("&")).toList();
        for (String param : split) {
            if (!param.contains("=")) {
                throw new IllegalArgumentException();
            }

            final var pair = Arrays.stream(param.split("=", 2)).toList();
            if (pair.size() >= 3 || pair.isEmpty()) {
                throw new IllegalArgumentException();
            }

            final var key = pair.getFirst().trim();
            var value = pair.getLast().trim();
            if (pair.size() == 1 && param.indexOf("=") == param.length() - 1) {
                value = "";
            }
            result.put(key, value);
        }
        return Collections.unmodifiableMap(result);
    }
}
