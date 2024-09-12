package org.apache.coyote.http11.request;

import static org.apache.coyote.http11.Http11Processor.HTTP_VERSION;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public record HttpRequestLine(
        HttpMethod httpMethod,
        String path,
        Map<String, String> queryString,
        String version) {

    HttpRequestLine(String requestLine) {
        this(
                extractHttpMethod(requestLine),
                extractPath(requestLine),
                extractQueryString(requestLine),
                extractVersion(requestLine)
        );
    }

    private static HttpMethod extractHttpMethod(String requestLine) {
        return HttpMethod.valueOf(requestLine.split(" ")[0]);
    }

    private static String extractPath(String requestLine) {
        return requestLine
                .split(" ")[1]
                .split("[?]")[0];
    }

    private static Map<String, String> extractQueryString(String requestLine) {
        String url = requestLine.split(" ")[1];

        Map<String, String> map = new HashMap<>();
        if (url.contains("?")) {
            String[] queryStringArgs = url
                    .split("[?]")[1]
                    .split("&");
            Arrays.stream(queryStringArgs)
                    .map(param -> param.split("="))
                    .filter(parts -> parts.length == 2)
                    .forEach(parts -> map.put(parts[0], parts[1]));
        }

        return map;
    }

    private static String extractVersion(String requestLine) {
        String version = requestLine.split(" ")[2];
        if (!HTTP_VERSION.equals(version)) {
            throw new IllegalArgumentException("Invalid HTTP version: " + version);
        }

        return version;
    }
}
