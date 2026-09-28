package org.apache.coyote.http11;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpResponse {

    private static final String HTTP_VERSION = "HTTP/1.1";

    private int statusCode = 200;
    private String reasonPhrase = "OK";
    private final Map<String, String> headers = new LinkedHashMap<>();
    private byte[] bodyBytes = new byte[0];

    public void setStatus(final int statusCode, final String reasonPhrase){
        this.statusCode = statusCode;
        this.reasonPhrase = reasonPhrase;
    }

    public void setHeader(final String name, final String value) {
        headers.put(name, value);
    }

    public void setBody(final byte[] bodyBytes) {
        this.bodyBytes = bodyBytes.clone();
    }

    public void addCookie(final String name, final String value) {
        headers.put("Set-Cookie", name + "=" + value);
    }

    public void sendRedirect(final String location) {
        statusCode = 302;
        reasonPhrase = "Found";
        headers.put("Location", location);
        bodyBytes = new byte[0];
    }

    public void writeTo(final OutputStream outputStream) throws IOException {
        final StringBuilder responseHeaders = new StringBuilder();

        appendStatusLine(responseHeaders);
        appendHeaders(responseHeaders);

        // Header와 Body 사이의 빈 줄
        responseHeaders.append("\r\n");

        outputStream.write(responseHeaders
                .toString()
                .getBytes(StandardCharsets.UTF_8));

        outputStream.write(bodyBytes);
        outputStream.flush();
    }

    private void appendStatusLine(final StringBuilder responseHeaders) {
        responseHeaders.append(HTTP_VERSION)
                .append(" ")
                .append(statusCode)
                .append(" ")
                .append(reasonPhrase)
                .append("\r\n");
    }

    private void appendHeaders(final StringBuilder responseHeaders) {
        for (final Map.Entry<String, String> header
                : headers.entrySet()) {
            responseHeaders.append(header.getKey())
                    .append(": ")
                    .append(header.getValue())
                    .append("\r\n");
        }

        responseHeaders.append("Content-Length: ")
                .append(bodyBytes.length)
                .append("\r\n");
    }
}
