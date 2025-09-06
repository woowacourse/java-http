package org.apache.coyote.http11.message.response;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import org.apache.coyote.http11.message.HttpBody;
import org.apache.coyote.http11.message.HttpHeaders;

public class HttpResponse {
    private final HttpStatus status;
    private final HttpHeaders headers;
    private final HttpBody body;

    public HttpResponse(HttpStatus status, HttpHeaders headers, HttpBody body) {
        this.status = status;
        this.headers = headers;
        this.body = body;
    }

    public int getStatusCode() {
        return status.getCode();
    }

    public String getReasonPhrase() {
        return status.getReasonPhrase();
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public HttpBody getBody() {
        return body;
    }

    public void writeTo(OutputStream output) throws IOException {
        output.write(getHeaderText().getBytes(StandardCharsets.ISO_8859_1));
        output.write(getBodyBytes());
    }

    // 헤더만 문자열로 변환
    private String getHeaderText() {
        StringBuilder sb = new StringBuilder();
        sb.append("HTTP/1.1 ")
                .append(status.getCode())
                .append(" ")
                .append(status.getReasonPhrase())
                .append("\r\n");

        headers.getLines().forEach(line -> sb.append(line).append("\r\n"));
        sb.append("\r\n");
        return sb.toString();
    }

    // 바디를 바이트 배열로 변환
    private byte[] getBodyBytes() {
        return body.getBytes();
    }
}
