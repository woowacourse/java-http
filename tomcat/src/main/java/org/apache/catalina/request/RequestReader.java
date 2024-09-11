package org.apache.catalina.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestReader {
    private static final Logger log = LoggerFactory.getLogger(RequestReader.class);
    private static final String PARAM_SEPARATOR = "&";
    private static final String QUERY_KEY_VALUE_DELIMITER = "=";

    public static Request readHeaders(BufferedReader reader) {
        List<String> headerLines = readHeaderLines(reader);
        validateHeaderLines(headerLines);

        Map<String, String> headers = parseHeaders(headerLines);
        Request request = new Request(headerLines.getFirst(), headers);
        request.setBody(getBody(reader, request.getContentLength()));
        return request;
    }

    private static List<String> readHeaderLines(BufferedReader reader) {
        List<String> headerLines = new ArrayList<>();
        try {
            String line;
            while ((line = reader.readLine()) != null && !line.isBlank()) {
                headerLines.add(line);
            }
        } catch (IOException e) {
            log.error("요청 헤더를 읽는 도중 오류가 발생했습니다.");
            throw new RuntimeException("요청 헤더를 읽는 도중 오류가 발생했습니다.");
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

    private static Map<String, String> getBody(BufferedReader reader, int contentLength) {
        char[] buffer = new char[contentLength];
        try {
            int readChars = reader.read(buffer, 0, contentLength);
            if (readChars < contentLength) {
                throw new IllegalArgumentException("실제 읽은 바이트 수가 예상된 길이보다 작습니다.");
            }
        } catch (IOException e) {
            throw new RuntimeException("요청 본문을 읽는 도중 오류가 발생했습니다.", e);
        }
        return getParamValues(new String(buffer));
    }

    private static Map<String, String> getParamValues(String params) {
        return Arrays.stream(params.split(PARAM_SEPARATOR))
                .map(param -> param.split(QUERY_KEY_VALUE_DELIMITER, 2))
                .filter(parts -> parts.length == 2 && parts[1] != null)
                .collect(Collectors.toMap(
                        parts -> parts[0].trim(),
                        parts -> parts[1].trim()
                ));
    }
}
