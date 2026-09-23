package org.apache.coyote.http11;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HttpResponse {

    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String LOCATION = "Location";

    private final int statusCode;
    private final String statusText;
    private final Map<String, List<String>> headers;
    private final byte[] body;

    private HttpResponse(
            int statusCode,
            String statusText,
            Map<String, List<String>> headers,
            byte[] body
    ) {
        this.statusCode = statusCode;
        this.statusText = statusText;
        this.headers = new LinkedHashMap<>();
        headers.forEach((name, values) -> this.headers.put(name, new ArrayList<>(values)));
        this.body = body;
    }

    public static HttpResponse ok(String contentType, byte[] body) {
        Map<String, List<String>> headers = new LinkedHashMap<>();
        headers.put(CONTENT_TYPE, List.of(contentType));
        headers.put(CONTENT_LENGTH, List.of(String.valueOf(body.length)));

        return new HttpResponse(
                200,
                "OK",
                headers,
                body
        );
    }

    public static HttpResponse redirect(String location) {
        return new HttpResponse(
                302,
                "Found",
                Map.of(
                        LOCATION, List.of(location),
                        CONTENT_LENGTH, List.of("0")
                ),
                new byte[0]
        );
    }

    public static HttpResponse notFound(byte[] body) {
        return new HttpResponse(
                404,
                "Not Found",
                Map.of(
                        CONTENT_TYPE, List.of("text/html;charset=utf-8"),
                        CONTENT_LENGTH, List.of(String.valueOf(body.length))
                ),
                body
        );
    }

    public void addHeader(String name, String value) {
        headers.computeIfAbsent(name, key -> new ArrayList<>())
                .add(value);
    }

    @Override
    public String toString() {
        StringBuilder response = new StringBuilder();

        response.append("HTTP/1.1 ")
                .append(statusCode)
                .append(" ")
                .append(statusText)
                .append(" \r\n");

        headers.forEach((name, values) ->
                values.forEach(value ->
                        response.append(name)
                                .append(": ")
                                .append(value)
                                .append(" \r\n")
                )
        );

        return response.append("\r\n")
                .append(new String(body, StandardCharsets.UTF_8))
                .toString();
    }
}
