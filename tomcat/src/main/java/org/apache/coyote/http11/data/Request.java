package org.apache.coyote.http11.data;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public record Request(
            RequestPoint requestPoint,
            Map<String, String> requestHeaders,
            Map<String, String> queryParameters,
            String requestBody) {

    public static Request from(final byte[] bytes) throws IOException {
        final String[] request = new String(bytes, StandardCharsets.UTF_8).split("\r\n\r\n");
        final String requestHeader = request[0];
        final String requestBody = request.length > 1 ? request[1] : "";

        boolean isFirstLine = true;
        RequestPoint requestEndPoint = null;
        final Map<String, String> requestHeaderMap = new HashMap<>();
        for (String line : requestHeader.split("\r\n")) {

            if (isFirstLine) {
                requestEndPoint = RequestPoint.from(line);
                isFirstLine = false;
                continue;
            }

            addHeader(requestHeaderMap, line);
        }


        final Map<String, String> queryParameters = parseQueryParameter(requestEndPoint.query());

        return new Request(requestEndPoint, requestHeaderMap, queryParameters, requestBody);
    }

    private static Map<String, String> parseQueryParameter(final String query) {
        if (query == null || query.isEmpty()) {
            return Map.of();
        }

        final String[] parameters = query.split("&");

        return Arrays.stream(parameters)
                .map(parameter -> parameter.split("="))
                .collect(
                        HashMap::new,
                        (map, keyValue) -> map.put(keyValue[0], keyValue[1]),
                        HashMap::putAll
                );
    }

    private static void addHeader(
            final Map<String, String> requestHeaderMap,
            final String headerLine) {
        final String[] headerField = headerLine.split("\\s?:\\s?");
        requestHeaderMap.put(headerField[0], headerField[1]);
    }

    public record RequestPoint(
            String method,
            String path,
            String query,
            String version) {

        public static RequestPoint from(final String requestLine) {
            final String[] requestLineParts = requestLine.split(" ");
            final String[] split = requestLineParts[1].split("\\?");
            final String path = split[0];
            final String query = split.length > 1 ? split[1] : "";

            return new RequestPoint(
                    requestLineParts[0],
                    path,
                    query,
                    requestLineParts[2]
            );
        }

    }

}
