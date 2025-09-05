package org.apache.coyote.http11;

import java.nio.charset.StandardCharsets;
import org.apache.coyote.http11.constant.ContentType;
import org.apache.coyote.http11.constant.HttpStatus;

public record HttpResponse(
        HttpStatus statusCode,
        ContentType contentType,
        String body
) {
    public byte[] convertByteArray() {
        return convertString().getBytes(StandardCharsets.UTF_8);
    }

    private String convertString() {
        return body == null ? convertNonContainsBody() : convertContainsBody();
    }

    private String convertNonContainsBody() {
        return String.join("\r\n",
                String.format("HTTP/1.1 %s %s", statusCode.getCode(), statusCode.getMessage()),
                String.format("Content-Type: %s;charset=utf-8", contentType.getResponseContentType()),
                ""
        );
    }

    private String convertContainsBody() {
        return String.join("\r\n",
                String.format("HTTP/1.1 %s %s", statusCode.getCode(), statusCode.getMessage()),
                String.format("Content-Type: %s;charset=utf-8", contentType.getResponseContentType()),
                String.format("Content-Length: %d", body.getBytes(StandardCharsets.UTF_8).length),
                "",
                body
        );
    }
}
