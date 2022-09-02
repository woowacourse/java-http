package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class HttpRequestHeaderFactory {

    public static HttpRequestHeader parse(java.io.InputStream inputStream) throws IOException {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        Map<String, String> requestHeaders = new HashMap<>();
        final String requestLine = reader.readLine();
        String line = reader.readLine();
        while (!"".equals(line)) {
            parseRequestHeader(requestHeaders, line);
            line = reader.readLine();
            if (line == null) {
                return null;
            }
        }
        return HttpRequestHeader.of(requestLine, requestHeaders);
    }

    private static void parseRequestHeader(Map<String, String> headerFields, String line) {
        String[] parsedHeaderField = line.split(": ");
        if (parsedHeaderField.length != 2) {
            throw new IllegalArgumentException("헤더는 속성과 정보 두 가지로 이루어 집니다.");
        }
        headerFields.put(parsedHeaderField[0], parsedHeaderField[1]);
    }
}
