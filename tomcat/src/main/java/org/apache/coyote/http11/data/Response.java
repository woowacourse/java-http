package org.apache.coyote.http11.data;

import java.util.HashMap;
import java.util.Map;

public class Response {
    private final int statusCode;
    private final Map<String, String> headers;
    private final String body;
    private Cookies cookies;

    private Response(
            int statusCode,
            Map<String, String> responseHeaderMap,
            String body, Cookies cookies
    ) {
        this.statusCode = statusCode;
        this.headers = new HashMap<>(responseHeaderMap);
        this.body = body;
        this.cookies = cookies;
    }

    private Response(
            int statusCode,
            Map<String, String> responseHeaderMap,
            String body
    ) {
        this(statusCode, responseHeaderMap, body, Cookies.empty());
    }

    private static final Map<Integer, String> httpStatusMessage = new HashMap<>() {
        {
            // TODO: Add more status codes and messages as needed
            put(200, "OK");
            put(204, "No Content");
            put(302, "Found");
            put(400, "Bad Request");
            put(404, "Not Found");
            put(500, "Internal Server Error");
        }
    };
    private static final String CRLF = " \r\n";


    public static Response noContent() {
        return new Response(204, new HashMap<>(), "");
    }

    public static Response ok() {
        return new Response(200, new HashMap<>(), "");
    }

    public static Response ok(
            final Map<String, String> responseHeaderMap,
            final String responseBody) {

        return new Response(200, responseHeaderMap, responseBody);
    }

    public static Response notFound() {
        return new Response(404, new HashMap<>(), "Not Found");
    }

    public static Response badRequest() {
        return new Response(400, new HashMap<>(), "Bad Request");
    }

    public static Response redirect(final String location) {
        return new Response(302, Map.of("Location", location), "");
    }

    public void setCookies(final Cookies cookies) {
        this.cookies = cookies;
    }

    public Cookies getCookies() {
        return cookies;
    }

    public void addCookie(Cookie cookie) {
        if (cookies == null) {
            cookies = Cookies.of(cookie);
        } else {
            cookies = cookies.with(cookie);
        }
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder()
                .append("HTTP/1.1 ")
                .append(statusCode)
                .append(" ")
                .append(httpStatusMessage.get(statusCode))
                .append(CRLF);

        for (var entry : headers.entrySet()) {
            sb.append(entry.getKey())
                    .append(": ")
                    .append(entry.getValue())
                    .append(CRLF);
        }

        for (Cookie cookie : cookies.values()) {
            sb.append("Set-Cookie: ")
                    .append(cookie)
                    .append(CRLF);
        }

        sb.append("Content-Length: ")
                .append(body.getBytes().length)
                .append(CRLF);

        return sb.append("\r\n")
                .append(body)
                .toString();
    }
}
