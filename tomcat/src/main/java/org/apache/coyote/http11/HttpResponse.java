package org.apache.coyote.http11;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public final class HttpResponse {

    private static final String HTTP_VERSION = "HTTP/1.1";

    private final HttpStatus status;
    private final Map<String, String> headers;
    private final byte[] body;

    public HttpResponse(
            HttpStatus status,
            Map<String, String> headers,
            String body
    ) {
        this.status = Objects.requireNonNull(status);
        Map<String, String> copiedHeaders = new LinkedHashMap<>(Objects.requireNonNull(headers));
        if (copiedHeaders.keySet().stream().anyMatch("Content-Length"::equalsIgnoreCase)) {
            throw new IllegalArgumentException("Content-Length는 응답 본문으로부터 계산됩니다.");
        }
        this.headers = Collections.unmodifiableMap(copiedHeaders);
        this.body = Objects.requireNonNull(body).getBytes(StandardCharsets.UTF_8);
    }

    public void writeTo(OutputStream outputStream) throws IOException {
        Objects.requireNonNull(outputStream);

        outputStream.write(statusLine().getBytes(StandardCharsets.UTF_8));
        for (Map.Entry<String, String> header : headers.entrySet()) {
            outputStream.write(headerLine(header).getBytes(StandardCharsets.UTF_8));
        }
        outputStream.write(("Content-Length: " + body.length + "\r\n\r\n")
                .getBytes(StandardCharsets.UTF_8));
        outputStream.write(body);
        outputStream.flush();
    }

    private String statusLine() {
        return HTTP_VERSION
                + " " + status.getCode()
                + " " + status.getMessage()
                + "\r\n";
    }

    private String headerLine(Map.Entry<String, String> header) {
        return header.getKey() + ": " + header.getValue() + "\r\n";
    }
}
