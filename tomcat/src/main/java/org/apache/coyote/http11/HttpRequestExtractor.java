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
        String httpMethod = extractHttpMethod(requestLine);
        String uri = extractUri(requestLine);
        Map<String, String> queryParams = extractQueryParams(uri);
        Map<String, String> headers = extractHeaders(br);
        String body = extractBody(br);
        // TODO: 메서드 호출 순서에 의존

        return new HttpRequest(httpMethod, uri, queryParams, headers, body);
    }

    private static String extractHttpMethod(String requestLine) {
        return requestLine.split(" ")[0];
    }

    private static String extractUri(String requestLine) {
        return requestLine.split(" ")[1];
    }

    private static Map<String, String> extractQueryParams(String requestUri) {
        Map<String, String> queryParams = new HashMap<>();
        if (!requestUri.contains("?")) {
            return queryParams;
        }
        String queryString = requestUri.split("\\?")[1];
        String[] rawQueryParams = queryString.split("&");
        for (String rawQueryParam : rawQueryParams) {
            String key = rawQueryParam.split("=")[0];
            String value = rawQueryParam.split("=")[1];
            queryParams.put(key, value);
        }
        return queryParams;
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
            char[] one = Character.toChars(br.read());
            sb.append(one);
        }
        return sb.toString();
    }
}
