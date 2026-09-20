package org.apache.coyote.http11.data;

import java.util.HashMap;
import java.util.Map;

public record Response(
            int statusCode,
            Map<String, String> responseHeaderMap,
            String responseBody) {

    private static final Map<Integer, String> httpStatusMessage = new HashMap<>() {
        {
            // TODO: Add more status codes and messages as needed
            put(200, "OK");
            put(204, "No Content");
            put(400, "Bad Request");
            put(404, "Not Found");
            put(500, "Internal Server Error");
        }
    };
    private static final String CRLF = " \r\n";

    public static Response noContent() {
        return new Response(204, Map.of(), "");
    }

    public static Response ok() {
        return new Response(200, Map.of(), "");
    }

    public static Response ok(
            final Map<String, String> responseHeaderMap,
            final String responseBody) {

        return new Response(200, responseHeaderMap, responseBody);
    }

    public static Response notFound() {
        return new Response(404, Map.of(), "Not Found");
    }

    public static Response badRequest() {
        return new Response(400, Map.of(), "Bad Request");
    }

    public static Response redirect(final String location) {
        return new Response(302, Map.of("Location", location), "");
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder()
                .append("HTTP/1.1 ")
                .append(statusCode)
                .append(" ")
                .append(httpStatusMessage.get(statusCode))
                .append(CRLF);

        for (var entry : responseHeaderMap.entrySet()) {
            sb.append(entry.getKey())
                    .append(": ")
                    .append(entry.getValue())
                    .append(CRLF);
        }

        sb.append("Content-Length: ")
                .append(responseBody.getBytes().length)
                .append(CRLF);

        return sb.append("\r\n")
                .append(responseBody)
                .toString();
    }
}
