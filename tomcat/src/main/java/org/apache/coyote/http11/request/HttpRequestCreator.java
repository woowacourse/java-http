package org.apache.coyote.http11.request;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class HttpRequestCreator {

    private HttpRequestCreator() {
    }

    public static HttpRequest createHttpRequest(BufferedReader reader) throws IOException {
        HttpRequestStartLine startLine = createStartLine(reader.readLine());

        List<String> headersText = readHeaders(reader);
        HttpRequestHeaders headers = createHeaders(headersText);

        String contentLength = headers.getHeader("Content-Length").orElse("0");
        String body = readBody(reader, contentLength);
        return new HttpRequest(startLine, headers, body);
    }

    private static HttpRequestStartLine createStartLine(String startLineText) {
        String[] startLineComponents = startLineText.split(" ");
        if (startLineComponents.length != 3) {
            throw new UncheckedServletException("요청 시작 라인의 형식이 일치하지 않습니다.");
        }
        return new HttpRequestStartLine(startLineComponents[0], startLineComponents[1], startLineComponents[2]);
    }

    private static List<String> readHeaders(BufferedReader reader) throws IOException {
        final List<String> lines = new LinkedList<>();
        String line;
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            lines.add(line);
        }
        return lines;
    }

    private static HttpRequestHeaders createHeaders(List<String> headersText) throws IOException {
        Map<String, String> headers = new HashMap<>();
        for (String headerText : headersText) {
            String headerKey = headerText.split(": ")[0].trim();
            String headerValue = headerText.split(": ")[1].trim();
            headers.put(headerKey, headerValue);
        }
        return new HttpRequestHeaders(headers);
    }

    private static String readBody(BufferedReader reader, String contentLength) throws IOException {
        try {
            int bodyLength = Integer.parseInt(contentLength);
            char[] buffer = new char[bodyLength];
            reader.read(buffer, 0, bodyLength);
            return String.valueOf(buffer);
        } catch (NumberFormatException e) {
            return "";
        }
    }
}
