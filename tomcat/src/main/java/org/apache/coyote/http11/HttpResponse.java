package org.apache.coyote.http11;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class HttpResponse {
    private final List<Header> headers = new ArrayList<>();
    private HttpStatus httpStatus = HttpStatus.OK;
    private String body = "";

    public byte[] toBytes() {
        StringBuilder response = new StringBuilder();

        response.append("HTTP/1.1 ")
                .append(httpStatus.getStatusCode())
                .append(" ")
                .append(httpStatus.getStatusMessage())
                .append(" ")
                .append("\r\n");

        for (Header header : headers) {
            response.append(header.getKey())
                    .append(": ")
                    .append(header.getValue())
                    .append("\r\n");
        }

        response.append("Content-Length: ")
                .append(body.getBytes(StandardCharsets.UTF_8).length)
                .append(" ")
                .append("\r\n\r\n")
                .append(body);

        return response.toString().getBytes(StandardCharsets.UTF_8);
    }

    public HttpResponse status(HttpStatus status) {
        this.httpStatus = status;
        return this;
    }

    public HttpResponse contentType(String type) {
        addHeader("Content-Type", "text/" + type + ";charset=utf-8 ");
        return this;
    }

    public HttpResponse body(String body) {
        if (body == null) {
            body = "";
        }
        this.body = body;
        return this;
    }

    public HttpResponse redirectTo(String location) {
        this.httpStatus = HttpStatus.FOUND;
        addHeader("Location", location);
        return this;
    }

    public HttpResponse addCookie(String name, String value) {
        addHeader("Set-Cookie", name + "=" + value);
        return this;
    }

    public HttpResponse expiresCookie(String name, String path) {
        addHeader("Set-Cookie", name + "=; Max-Age=0; Path=" + path);
        return this;
    }

    public HttpResponse addHeader(String key, String value) {
        headers.add(new Header(key, value));
        return this;
    }
}
