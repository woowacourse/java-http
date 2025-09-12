package org.apache.coyote.http11.http.request.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.apache.coyote.http11.http.HttpMethod;
import org.apache.coyote.http11.http.request.RequestLine;
import org.apache.coyote.http11.http.request.dto.HttpRequest;
import org.apache.coyote.http11.http.HttpHeaders;

public final class HttpRequestParser {

    private HttpRequestParser() {
    }

    public static HttpRequest parse(InputStream inputStream) throws IOException {
        Head head = readHead(inputStream);

        RequestLine requestLine = new RequestLine(head.startLine);
        HttpHeaders headers = parseHeaders(head.headerLines);
        Map<String, String> queryParams = QueryStringParser.parse(requestLine.queryString());
        Map<String, String> bodyParams = parseBody(requestLine.method(), headers, head.bodyReader);

        return new HttpRequest(
                requestLine,
                headers,
                queryParams,
                bodyParams,
                Map.of()
        );
    }

    private static Head readHead(InputStream inputStream) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.ISO_8859_1));

        String start = br.readLine();
        if (start == null || start.isEmpty()) {
            throw new IOException("Empty request line");
        }

        List<String> headerLines = new ArrayList<>();
        String line;
        while ((line = br.readLine()) != null) {
            if (line.isEmpty()) break;
            headerLines.add(line);
        }
        return new Head(start, headerLines, br);
    }

    private static HttpHeaders parseHeaders(List<String> lines) {
        HttpHeaders headers = new HttpHeaders();
        for (String line : lines) {
            String[] parts = line.split(":", 2);
            if (parts.length == 2) {
                headers.add(parts[0].strip(), parts[1].strip());
            }
        }
        return headers;
    }

    private static Map<String, String> parseBody(HttpMethod method, HttpHeaders headers, BufferedReader bodyReader) throws IOException {
        if (!(method == HttpMethod.POST || method == HttpMethod.PUT || method == HttpMethod.PATCH)) {
            return Map.of();
        }

        int contentLength = headers.getFirst("Content-Length")
                .map(v -> {
                    try { return Integer.parseInt(v.trim()); }
                    catch (NumberFormatException e) { return 0; }
                }).orElse(0);

        if (contentLength <= 0) return Map.of();

        char[] buf = new char[contentLength];
        int read = bodyReader.read(buf);
        String body = new String(buf, 0, read);

        String contentType = headers.getFirst("Content-Type").orElse("").toLowerCase(Locale.ROOT);
        if (contentType.startsWith("application/x-www-form-urlencoded")) {
            return QueryStringParser.parse(body);
        }
        return Map.of();
    }

    private record Head(String startLine, List<String> headerLines, BufferedReader bodyReader) {}
}
