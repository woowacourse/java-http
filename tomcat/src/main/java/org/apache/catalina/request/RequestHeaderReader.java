package org.apache.catalina.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHeaderReader {
    private static final Logger log = LoggerFactory.getLogger(RequestHeaderReader.class);

    public static Map<String, String> readHeaders(BufferedReader reader) {
        List<String> headerLines = readHeaderLines(reader);
        validateHeaderLines(headerLines);

        Map<String, String> headers = parseHeaders(headerLines);
        addRequestLineDetails(headerLines.get(0), headers);

        return headers;
    }

    private static List<String> readHeaderLines(BufferedReader reader) {
        List<String> headerLines = new ArrayList<>();
        try {
            String line;
            while ((line = reader.readLine()) != null && !line.isBlank()) {
                headerLines.add(line);
            }
        } catch (IOException e) {
            log.error("요청 헤더를 읽는 도중 오류가 발생했습니다.", e);
            throw new RuntimeException("요청 헤더를 읽는 도중 오류가 발생했습니다.", e);
        }
        return headerLines;
    }

    private static void validateHeaderLines(List<String> headerLines) {
        if (headerLines.isEmpty()) {
            throw new IllegalArgumentException("요청 헤더가 비어 있습니다.");
        }
    }

    private static Map<String, String> parseHeaders(List<String> headerLines) {
        return headerLines.stream()
                .skip(1)
                .map(line -> line.split(": ", 2))
                .filter(parts -> parts.length == 2)
                .collect(Collectors.toMap(parts -> parts[0], parts -> parts[1]));
    }

    private static void addRequestLineDetails(String requestLine, Map<String, String> headers) {
        String[] parts = requestLine.split(" ");
        if (parts.length < 2) {
            throw new IllegalArgumentException("요청 헤더의 형식이 올바르지 않습니다.");
        }
        headers.put("HttpMethod", parts[0]);
        headers.put("Url", parts[1]);
        headers.put("Accept", extractMainFileType(headers.get("Accept")));
    }

    private static String extractMainFileType(String acceptHeader) {
        if (acceptHeader == null) {
            return "text/html";
        }
        String[] types = acceptHeader.split(",");
        return types[0].trim();
    }
}
