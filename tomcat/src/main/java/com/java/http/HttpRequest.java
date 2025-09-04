package com.java.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public record HttpRequest(
        HttpMethod method,
        String uri,
        Map<String, String> queryParameter
) {
    public static HttpRequest from(InputStream inputStream) throws IOException {
        List<String> strings = readInputStream(inputStream);
        String[] headers = strings.getFirst().split(" ");
        String method = headers[0];
        Map<String, String> paramMap = new HashMap<>();
        String uri;
        int queryParamIndex = headers[1].indexOf("?");
        if (queryParamIndex == -1) {
            uri = headers[1];
        } else {
            uri = headers[1].substring(0, queryParamIndex);
            String[] params = headers[1].substring(queryParamIndex + 1).split("&");
            Arrays.stream(params).forEach(param -> {
                String[] data = param.split("=");
                paramMap.put(data[0], data[1]);
            });
        }

        return new HttpRequest(HttpMethod.parse(method), uri, paramMap);
    }

    private static List<String> readInputStream(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

        List<String> result = new ArrayList<>();
        String line;
        while ((line = reader.readLine()) != null) {
            result.add(line);
            if (line.isEmpty()) {
                break;
            }
        }

        return result;
    }

    public String param(String key) {
        return queryParameter.get(key);
    }

    public enum HttpMethod {
        GET,
        POST,
        PUT,
        PATCH,
        DELETE,
        OPTIONS,
        ;

        public static HttpMethod parse(String str) {
            return Arrays.stream(HttpMethod.values())
                    .filter(value -> value.name().equalsIgnoreCase(str))
                    .findFirst()
                    .orElseThrow();
            // TODO : 명확한 예외 타입 사용
        }
    }
}
