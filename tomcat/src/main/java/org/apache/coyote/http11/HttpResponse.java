package org.apache.coyote.http11;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public record HttpResponse(
        HttpStatus httpStatus,
        Header header,
        byte[] responseBody
) {

    private static final String RESPONSE_HEADER_FORMAT = "%s: %s \r\n";

    public byte[] serialize() {
        String message = String.join("\r\n", getStartLine(), getHeaders(), getBody());
        return message.getBytes();
    }

    private String getStartLine() {
        return "HTTP/1.1 " + httpStatus.getDescription() + " ";
    }

    private CharSequence getHeaders() {
        StringBuilder stringBuilder = new StringBuilder();
        Map<String, String> headerMap = header.getHeader();
        headerMap.forEach((key, value) -> stringBuilder.append(String.format(RESPONSE_HEADER_FORMAT, key, value)));
        String format = String.format(RESPONSE_HEADER_FORMAT, "Content-Length", responseBody.length);
        stringBuilder.append(format);

        return stringBuilder;
    }

    private String getBody() {
        return new String(responseBody, StandardCharsets.UTF_8);
    }
}
