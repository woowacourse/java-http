package org.apache.coyote.http11.response;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpResponse {

    private static final String HTTP_VERSION = "HTTP/1.1";

    private final int statusCode;
    private final String statusMessage;
    private final Map<String, String> headers = new LinkedHashMap<>();
    private final byte[] body;

    public HttpResponse(final int statusCode, final String statusMessage, final byte[] body) {
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
        this.body = body;
    }

    public void addHeader(final String name, final String value) {
        headers.put(name, value);
    }

    public void writeTo(final OutputStream outputStream) throws IOException {
        final var responseHead = new StringBuilder();

        responseHead.append(HTTP_VERSION)
                .append(" ")
                .append(statusCode)
                .append(" ")
                .append(statusMessage)
                .append("\r\n");

        for (final var header : headers.entrySet()) {
            responseHead.append(header.getKey())
                    .append(": ")
                    .append(header.getValue())
                    .append("\r\n");
        }

        responseHead.append("\r\n");

        outputStream.write(responseHead.toString().getBytes(StandardCharsets.US_ASCII));
        outputStream.write(body);
        outputStream.flush();
    }
}
