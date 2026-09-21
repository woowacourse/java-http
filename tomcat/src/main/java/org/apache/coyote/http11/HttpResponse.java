package org.apache.coyote.http11;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public class HttpResponse {

    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String LOCATION = "Location";
    private static final String SET_COOKIE = "Set-Cookie";
    private static final String SESSION_COOKIE_NAME = "JSESSIONID";

    private final int statusCode;
    private final String statusText;
    private final Map<String, String> headers;
    private final byte[] body;

    private HttpResponse(
            int statusCode,
            String statusText,
            Map<String, String> headers,
            byte[] body
    ) {
        this.statusCode = statusCode;
        this.statusText = statusText;
        this.headers = Map.copyOf(headers);
        this.body = body;
    }

    public static HttpResponse ok(String contentType, byte[] body) {
        return new HttpResponse(
                200,
                "OK",
                Map.of(
                        CONTENT_TYPE, contentType,
                        CONTENT_LENGTH, String.valueOf(body.length)
                ),
                body
        );
    }

    public static HttpResponse redirect(String location) {
        return new HttpResponse(
                302,
                "Found",
                Map.of(
                        LOCATION, location,
                        CONTENT_LENGTH, "0"
                ),
                new byte[0]
        );
    }

    public static HttpResponse redirectWithSession(String location, String sessionId) {
        String cookie = SESSION_COOKIE_NAME
                + "="
                + sessionId
                + "; Path=/"
                + "; HttpOnly"
                + "; SameSite=Lax";

        return new HttpResponse(
                302,
                "Found",
                Map.of(
                        LOCATION, location,
                        SET_COOKIE, cookie,
                        CONTENT_LENGTH, "0"
                ),
                new byte[0]
        );
    }

    public static HttpResponse notFound(byte[] body) {
        return new HttpResponse(
                404,
                "Not Found",
                Map.of(
                        CONTENT_TYPE, "text/html;charset=utf-8",
                        CONTENT_LENGTH, String.valueOf(body.length)
                ),
                body
        );
    }

    @Override
    public String toString() {
        StringBuilder response = new StringBuilder();

        response.append("HTTP/1.1 ")
                .append(statusCode)
                .append(" ")
                .append(statusText)
                .append("\r\n");

        headers.forEach((name, value) ->
                response.append(name)
                        .append(": ")
                        .append(value)
                        .append("\r\n")
        );

        return response.append("\r\n")
                .append(new String(body, StandardCharsets.UTF_8))
                .toString();
    }
}
