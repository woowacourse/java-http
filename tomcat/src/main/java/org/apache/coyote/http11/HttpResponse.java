package org.apache.coyote.http11;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public record HttpResponse(
        StatusLine statusLine,
        Map<String, String> headers,
        byte[] body
) {

    public static HttpResponse redirect(String location) {
        return new HttpResponse(
                new StatusLine("HTTP/1.1", 302, "Found"),
                Map.of("Location", location),
                new byte[0]
        );
    }

    public static HttpResponse redirect(String location, Cookie cookie) {
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Location", location);
        headers.put("Set-Cookie", cookie.name() + "=" + cookie.value());

        return new HttpResponse(
                new StatusLine("HTTP/1.1", 302, "Found"),
                headers,
                new byte[0]
        );
    }

    public void writeTo(OutputStream outputStream) throws IOException {
        StringBuilder responseHead = new StringBuilder();
        responseHead.append(statusLine.toLine()).append("\r\n");

        for (Map.Entry<String, String> header : headers.entrySet()) {
            responseHead.append(header.getKey())
                    .append(": ")
                    .append(header.getValue())
                    .append("\r\n");
        }

        responseHead.append("Content-Length: ").append(body.length).append("\r\n\r\n");

        outputStream.write(responseHead.toString().getBytes(StandardCharsets.UTF_8));
        outputStream.write(body);
    }
}
