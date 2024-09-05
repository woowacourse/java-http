package org.apache.coyote;

import java.util.HashMap;
import java.util.Map;

public class HttpResponse {
    private static final double VERSION = 1.1;

    private final int statusCode;
    private final String statusMessage;
    private final Map<String, String> headers;
    private String body;

    public HttpResponse(int statusCode, String statusMessage) {
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
        this.headers = new HashMap<>();
        this.body = "";
    }

    public static HttpResponse redirect(String location) {
        return new HttpResponse(302, "Found")
                .addHeader("Location", location)
                .setBody("");
    }

    public HttpResponse addHeader(String key, String value) {
        headers.put(key, value);
        return this;
    }

    public HttpResponse setBody(String body) {
        this.body = body;
        headers.put("Content-Length", String.valueOf(body.getBytes().length));
        return this;
    }

    public HttpResponse addCookie(String key, String value) {
        if (headers.containsKey("Set-Cookie")) {
            headers.put("Set-Cookie", headers.get("Set-Cookie") + "; " + key + "=" + value);
            return this;
        }
        headers.put("Set-Cookie", key + "=" + value);
        return this;
    }

    public String toHttpMessage() {
        final var response = new StringBuilder();
        response.append("HTTP/").append(VERSION).append(" ").append(statusCode).append(" ").append(statusMessage).append("\r\n");
        headers.forEach((key, value) -> response.append(key).append(": ").append(value).append("\r\n"));
        response.append("\r\n");
        response.append(body);
        return response.toString();
    }

    public byte[] getAsBytes() {
        return toHttpMessage().getBytes();
    }
}
