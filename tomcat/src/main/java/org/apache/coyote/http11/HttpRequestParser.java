package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class HttpRequestParser {

    private HttpRequestParser() {
    }

    public static HttpRequest create(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        HttpRequestLine startLine = getStartLine(bufferedReader);
        Map<String, String> headers = getHeaders(bufferedReader);
        String body = getBody(headers, bufferedReader);
        Map<String, String> cookie = getCookie(headers);
        return new HttpRequest(startLine, headers, cookie, body);
    }

    private static HttpRequestLine getStartLine(BufferedReader bufferedReader) throws IOException {
        String[] lines = bufferedReader.readLine().split(" ");
        return new HttpRequestLine(lines[0], lines[1], lines[2]);
    }

    private static Map<String, String> getHeaders(BufferedReader bufferedReader) throws IOException {
        Map<String, String> header = new HashMap<>();
        String line = bufferedReader.readLine();
        while (line != null && !line.isBlank()) {
            String[] split = line.split(":");
            header.put(split[0], split[1].trim());
            line = bufferedReader.readLine();
        }
        return header;
    }

    private static String getBody(Map<String, String> headers, BufferedReader bufferedReader) throws IOException {
        if (!headers.containsKey("Content-Length")) {
            return "";
        }

        String contentLength = headers.get("Content-Length");
        int length = Integer.parseInt(contentLength);
        char[] buffer = new char[length];
        bufferedReader.read(buffer, 0, length);
        return new String(buffer);
    }

    private static Map<String, String> getCookie(Map<String, String> headers) {
        return Optional.ofNullable(headers.get("Cookie"))
            .map(cookieValue -> Arrays.stream(cookieValue.split("; "))
                .map(it -> it.split("="))
                .collect(Collectors.toMap(
                    keyAndValue -> keyAndValue[0],
                    keyAndValue -> keyAndValue[1])))
            .orElse(Collections.emptyMap());
    }
}
