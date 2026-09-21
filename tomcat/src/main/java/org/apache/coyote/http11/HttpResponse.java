package org.apache.coyote.http11;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public final class HttpResponse {

    private final String version;
    private final int statusCode;
    private final String statusMessage;
    private final Map<String, String> headers;
    private final byte[] body;

    public HttpResponse(
            String version,
            int statusCode,
            String statusMessage,
            Map<String, String> headers,
            String body
    ) {
        this.version = Objects.requireNonNull(version);
        this.statusCode = statusCode;
        this.statusMessage = Objects.requireNonNull(statusMessage);
        this.headers = new LinkedHashMap<>(Objects.requireNonNull(headers));
        if (this.headers.keySet().stream().anyMatch("Content-Length"::equalsIgnoreCase)) {
            throw new IllegalArgumentException("Content-Length는 응답 본문으로부터 계산됩니다.");
        }
        this.body = Objects.requireNonNull(body).getBytes(StandardCharsets.UTF_8);
    }

    public void writeTo(OutputStream outputStream) throws IOException {
        Objects.requireNonNull(outputStream);

        outputStream.write(statusLine().getBytes(StandardCharsets.UTF_8));
        for (Map.Entry<String, String> header : headers.entrySet()) {
            outputStream.write(headerLine(header).getBytes(StandardCharsets.UTF_8));
        }
        outputStream.write(("Content-Length: " + body.length + " \r\n\r\n")
                .getBytes(StandardCharsets.UTF_8));
        outputStream.write(body);
        outputStream.flush();
    }

    private String statusLine() {
        return version + " " + statusCode + " " + statusMessage + " \r\n";
    }

    private String headerLine(Map.Entry<String, String> header) {
        return header.getKey() + ": " + header.getValue() + "\r\n";
    }
}
