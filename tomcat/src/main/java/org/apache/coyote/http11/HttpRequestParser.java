package org.apache.coyote.http11;

import org.apache.coyote.HttpRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpRequestParser {

    private static final String COLON = ":";
    private static final String CONTENT_LENGTH = "Content-Length";

    public static HttpRequest parse(BufferedReader bufferedReader) throws IOException {
        String requestLine = readRequestLine(bufferedReader);
        String[] requestLines = requestLine.split(" ");
        Map<String, String> headers = readHeaders(bufferedReader);
        String body = readBody(bufferedReader, headers);
        return new HttpRequest(
                requestLines[0],
                requestLines[1],
                requestLines[2],
                headers,
                body
        );
    }

    private static String readRequestLine(BufferedReader bufferedReader) throws IOException {
        String requestLine = bufferedReader.readLine();
        if (requestLine == null) {
            throw new IllegalArgumentException(
                    "HTTP 요청 라인이 존재하지 않습니다."
            );
        }

        String[] tokens = requestLine.split(" ");
        if (tokens.length != 3) {
            throw new IllegalArgumentException(
                    "잘못된 HTTP 요청 라인입니다: " + requestLine
            );
        }
        return requestLine;
    }

    private static Map<String, String> readHeaders(final BufferedReader bufferedReader) throws IOException {
        Map<String, String> headers = new HashMap<>();
        while (true) {
            String line = bufferedReader.readLine();
            if (line == null || line.isEmpty()) {
                return headers;
            }
            String[] headerLine = line.split(COLON, 2);
            headers.put(headerLine[0], headerLine[1].trim());
        }
    }

    private static String readBody(final BufferedReader bufferedReader, final Map<String, String> headers) throws IOException {
        String contentLength = headers.get(CONTENT_LENGTH);
        if (contentLength == null) {
            return "";
        }
        char[] buffer = new char[Integer.parseInt(contentLength)];
        int count = bufferedReader.read(buffer, 0, buffer.length);
        return new String(buffer, 0, count);
    }
}
