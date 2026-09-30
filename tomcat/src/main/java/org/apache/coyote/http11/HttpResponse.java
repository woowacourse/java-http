package org.apache.coyote.http11;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public final class HttpResponse {

    private static final String CRLF = "\r\n";

    private final OutputStream outputStream;
    private final Map<String, String> headers = new LinkedHashMap<>();

    private int statusCode = 200;
    private String reasonPhrase = "OK";
    private byte[] body = new byte[0];

    public HttpResponse(final OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void setStatus(final int statusCode, final String reasonPhrase) {
        this.statusCode = statusCode;
        this.reasonPhrase = reasonPhrase;
    }

    public void addHeader(final String name, final String value) {
        if ("Content-Length".equalsIgnoreCase(name)) {
            throw new IllegalArgumentException("Content-Length는 응답 본문의 길이로 자동 계산합니다.");
        }
        headers.put(name, value);
    }

    public void setBody(final byte[] body) {
        this.body = body.clone();
    }

    public void write() throws IOException {
        final StringBuilder head = new StringBuilder();
        head.append("HTTP/1.1 ")
                .append(statusCode)
                .append(' ')
                .append(reasonPhrase)
                .append(CRLF);

        for (Map.Entry<String, String> header : headers.entrySet()) {
            head.append(header.getKey())
                    .append(": ")
                    .append(header.getValue())
                    .append(CRLF);
        }

        head.append("Content-Length: ")
                .append(body.length)
                .append(CRLF)
                .append(CRLF);

        outputStream.write(head.toString().getBytes(StandardCharsets.US_ASCII));
        outputStream.write(body);
        outputStream.flush();
    }
}
