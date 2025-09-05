package org.apache.coyote.http11.response;

import java.util.List;

public class StatusLine {

    private String protocol;
    private HttpStatus status;

    public StatusLine(String statusLine) {
        validateStatusLine(statusLine);

        String[] parsed = statusLine.trim().split(" ", 3);
        this.protocol = parsed[0];
        this.status = HttpStatus.from(Integer.parseInt(parsed[1]));
    }

    private void validateStatusLine(String statusLine) {
        if (statusLine == null || statusLine.isEmpty()) {
            throw new IllegalArgumentException("status line is empty");
        }

        String[] parsed = statusLine.trim().split(" ", 3);
        if (parsed.length != 3) {
            throw new IllegalArgumentException("length of status line is not 3");
        }
        
        String protocol = parsed[0];
        String statusCode = parsed[1];
        String reasonPhrase = parsed[2];

        // 1. protocol 검증 (HTTP 1.0과 1.1만 허용)
        if (!List.of("HTTP/1.0", "HTTP/1.1").contains(protocol)) {
            throw new IllegalArgumentException(protocol + ": invalid protocol");
        }

        // 2. status code와 reason phrase 검증
        HttpStatus status = HttpStatus.from(Integer.parseInt(statusCode));
        if (!status.getReasonPhrase().equals(reasonPhrase)) {
            throw new IllegalArgumentException(
                    "status code " + statusCode + " doesn't match reason phrase " + reasonPhrase
            );
        }
    }

    public String getProtocol() {
        return protocol;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public int getStatusCode() {
        return status.getHttpStatusCode();
    }

    public String getReasonPhrase() {
        return status.getReasonPhrase();
    }
}
