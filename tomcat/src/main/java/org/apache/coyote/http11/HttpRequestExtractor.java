package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class HttpRequestExtractor {

    public static HttpRequest extract(InputStream inputStream) throws IOException {
        InputStreamReader reader = new InputStreamReader(inputStream);
        BufferedReader br = new BufferedReader(reader);

        String requestLine = br.readLine();
        String httpMethod = extractMethod(requestLine);
        String uri = extractUri(requestLine);
        String version = extractVersion(requestLine);
        Map<String, String> headers = extractHeaders(br);
        String body = extractBody(br);
        // TODO: 메서드 호출 순서에 의존

        return new HttpRequest(version, httpMethod, uri, headers, body);
    }

    private static String extractMethod(String requestLine) {
        return requestLine.split(" ")[0];
    }

    private static String extractUri(String requestLine) {
        return requestLine.split(" ")[1];
    }

    private static String extractVersion(String requestLine) {
        return requestLine.split(" ")[2].split("/")[1];
    }

    private static Map<String, String> extractHeaders(BufferedReader br) throws IOException {
        Map<String, String> headers = new HashMap<>();
        while (br.ready()) {
            String rawHeader = br.readLine();
            if (!rawHeader.contains(": ")) {
                break;
            }
            String key = rawHeader.split(": ")[0];
            String value = rawHeader.split(": ")[1];
            headers.put(key, value);
        }
        return headers;
    }

    private static String extractBody(BufferedReader br) throws IOException {
        StringBuilder sb = new StringBuilder();
        while (br.ready()) {
            // 끝 개행이 없어 readLine 대신 read 사용
            char[] one = Character.toChars(br.read()); // TODO: refactoring
            sb.append(one);
        }
        return sb.toString();
    }
}
