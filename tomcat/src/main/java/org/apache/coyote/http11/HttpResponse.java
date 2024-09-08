package org.apache.coyote.http11;

import java.util.Map;

public record HttpResponse(
        HttpVersion httpVersion,
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
        return httpVersion.getVersionName() + " " + httpStatus.getDescription() + " ";
    }

    private CharSequence getHeaders() {
        StringBuilder stringBuilder = new StringBuilder();
        Map<String, String> headerMap = header.getHeader();
        headerMap.forEach((key, value) -> stringBuilder.append(String.format(RESPONSE_HEADER_FORMAT, key, value)));
        String format = String.format(RESPONSE_HEADER_FORMAT, HttpHeaderKey.CONTENT_LENGTH.getName(), responseBody.length);
        stringBuilder.append(format);

        return stringBuilder;
    }

    private String getBody() {
        return new String(responseBody);
    }
}
