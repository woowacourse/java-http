package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class RequestExtractor {

    public static Http11Request extract(InputStream inputStream) throws IOException {
        InputStreamReader reader = new InputStreamReader(inputStream);
        BufferedReader br = new BufferedReader(reader);

        String uri = extractUri(br);
        Map<String, String> queryParams = extractQueryParams(uri);
        Map<String, String> headers = extractHeaders(br);
        // TODO: 메서드 호출 순서에 의존

        return new Http11Request(uri, queryParams, headers);
    }

    private static String extractUri(BufferedReader br) throws IOException {
        String requestLine = br.readLine();
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
}
