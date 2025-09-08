package org.apache.coyote.http11;

import java.util.LinkedHashMap;
import java.util.Map;

public record HttpResponse(
        HttpStatus status,
        Map<String, String> headers,
        byte[] body
) {

    private static final String CRLF = "\r\n";
    private static final String RESPONSE_LINE_FORMAT = "%s %d %s\r\n";

    public String asString() {
        StringBuilder response = new StringBuilder();

        int statusCode = status.code();
        String reason = status.reason();
        response.append(String.format(RESPONSE_LINE_FORMAT, HttpVersion.HTTP_1_1.getName(), statusCode, reason));

        for (Map.Entry<String, String> header : headers().entrySet()) {
            response.append(header.getKey()).append(": ").append(header.getValue()).append(CRLF);
        }
        response.append(CRLF);
        response.append(new String(body));

        return response.toString();
    }

    public static HttpResponse redirect(String location) {
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Location", location);
        headers.put("Content-Length", "0");

        return new HttpResponse(HttpStatus.FOUND, headers, new byte[0]);
    }
}
