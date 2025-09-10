package org.apache.coyote.http11.response;

import org.apache.coyote.HttpStatus;
import org.apache.coyote.http11.MimeType;

import java.nio.charset.StandardCharsets;
import java.util.Map;

// 임시 클래스
public record HttpResponse(
        HttpStatus httpStatus,
        String body,
        MimeType mimeType,
        Map<String, String> headers
) {

    public String toHttpResponse() {
        StringBuilder builder = new StringBuilder();

        // status line
        builder.append("HTTP/1.1 ").append(httpStatus.getPhrase());
        builder.append("\r\n");

        // headers
        builder.append("Content-Type: ").append(mimeType.getMimeType()).append("\r\n");
        builder.append("Content-Length: ").append(body.getBytes(StandardCharsets.UTF_8).length).append("\r\n");
        for (Map.Entry<String, String> header : headers.entrySet()) {
            builder.append(header.getKey()).append(": ").append(header.getValue()).append("\r\n");
        }
        builder.append("\r\n");

        // body
        builder.append(body);
        return builder.toString();
    }
}
