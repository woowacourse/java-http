package org.apache.coyote.http11;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

public class HttpResponse {

    private static final String LINE_SEPARATOR = "\r\n";
    private static final String CONTENT_LENGTH_HEADER = "Content-Length";

    private final Map<String, String> headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    private HttpStatus status = HttpStatus.OK;
    private byte[] responseBody = new byte[0];

    public void setStatus(HttpStatus status) {
        this.status = Objects.requireNonNull(status, "응답 상태가 필요합니다.");
    }

    public void setHeader(String name, String value) {
        validateContentLengthNotSet(name);
        headers.put(name, value);
    }

    public void setCookie(String name, String value) {
        setHeader("Set-Cookie", name + "=" + value);
    }

    public void setBody(byte[] responseBody) {
        this.responseBody = responseBody.clone();
    }

    public void sendRedirect(String location) {
        setHeader("Location", location);
        status = HttpStatus.FOUND;
        responseBody = new byte[0];
    }

    public void writeTo(OutputStream outputStream) throws IOException {
        StringBuilder responseHead = new StringBuilder("HTTP/1.1 ")
                .append(status.getCode())
                .append(" ")
                .append(status.getReasonPhrase())
                .append(LINE_SEPARATOR);

        for (Map.Entry<String, String> header : headers.entrySet()) {
            responseHead.append(header.getKey())
                    .append(": ")
                    .append(header.getValue())
                    .append(LINE_SEPARATOR);
        }

        responseHead.append(CONTENT_LENGTH_HEADER)
                .append(": ")
                .append(responseBody.length)
                .append(LINE_SEPARATOR)
                .append(LINE_SEPARATOR);

        outputStream.write(responseHead.toString().getBytes(StandardCharsets.ISO_8859_1));
        outputStream.write(responseBody);
        outputStream.flush();
    }

    private void validateContentLengthNotSet(String name) {
        if (CONTENT_LENGTH_HEADER.equalsIgnoreCase(name)) {
            throw new IllegalArgumentException("Content-Length는 응답 본문에서 자동으로 계산합니다.");
        }
    }
}
