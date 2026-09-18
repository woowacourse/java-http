package org.apache.coyote.http11.data;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public record Request(
            RequestPoint requestPoint,
            Map<String, String> requestHeaderMap,
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

        return new Request(requestEndPoint, requestHeaderMap, requestBody);
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
            String version) {

        public static RequestPoint from(final String requestLine) {
            final String[] requestLineParts = requestLine.split(" ");
            return new RequestPoint(requestLineParts[0], requestLineParts[1], requestLineParts[2]);
        }

    }

}