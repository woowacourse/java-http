package org.apache.coyote.http11;

import java.util.Map;

public record HttpResponse(
        HttpStatus status,
        Map<String, String> headers,
        byte[] body
) {

    private static final String CRLF = "\r\n";

    public String asString() {
        StringBuilder response = new StringBuilder();

        int statusCode = status.code();
        String reason = status.reason();
        String responseLine = "HTTP/1.1 " + statusCode + " " + reason + CRLF;
        response.append(responseLine);

        for (Map.Entry<String, String> header : headers().entrySet()) {
            response.append(header.getKey()).append(": ").append(header.getValue()).append(CRLF);
        }
        response.append(CRLF);
        response.append(new String(body));

        return response.toString();
    }
}
