package org.apache.coyote.http11;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpResponse {

    private int status = 200;
    private String reason = "OK";
    private final Map<String, String> headers = new LinkedHashMap<>();
    private final List<String> cookies = new ArrayList<>();
    private byte[] body = new byte[0];

    public void setStatus(final int status, final String reason) {
        this.status = status;
        this.reason = reason;
    }

    public void setHeader(final String name, final String value) {
        if (name.equalsIgnoreCase("Set-Cookie")) {
            cookies.clear();
            cookies.add(value);
            return;
        }
        headers.put(headerName(name), value);
    }

    public void addCookie(final String name, final String value) {
        cookies.add(name + "=" + value);
    }

    private String headerName(final String name) {
        return headers.keySet().stream().filter(key -> key.equalsIgnoreCase(name)).findFirst().orElse(name);
    }

    public static HttpResponse error(final int status, final String reason) {
        final var response = new HttpResponse();
        response.setStatus(status, reason);
        response.setBody(reason, "text/plain");
        return response;
    }

    public void setBody(final String body, final String contentType) {
        setBody(body.getBytes(StandardCharsets.UTF_8), contentType + ";charset=utf-8");
    }

    public void setBody(final byte[] body, final String contentType) {
        this.body = body.clone();
        setHeader("Content-Type", contentType);
    }

    public void sendRedirect(final String location) {
        setStatus(302, "Found");
        setHeader("Location", location);
        headers.remove(headerName("Content-Type"));
        body = new byte[0];
    }

    public void writeTo(final OutputStream output) throws IOException {
        headers.remove(headerName("Content-Length"));
        final var head = new StringBuilder("HTTP/1.1 ")
                .append(status).append(' ').append(reason).append(" \r\n");
        headers.forEach((name, value) ->
                head.append(name).append(": ").append(value).append(" \r\n"));
        cookies.forEach(cookie -> head.append("Set-Cookie: ").append(cookie).append(" \r\n"));
        head.append("Content-Length: ").append(body.length).append(" \r\n\r\n");
        output.write(head.toString().getBytes(StandardCharsets.UTF_8));
        output.write(body);
        output.flush();
    }
}
