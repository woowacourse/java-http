package org.apache.coyote.http11;

import java.util.Map;

public record HttpResponse(
        int status,
        String reason,
        Map<String, String> headers,
        byte[] body
) {

    private static final String CRLF = "\r\n";

    public String asString() {
        StringBuilder headers = new StringBuilder();
        String responseLine = "HTTP/1.1 " + status + " " + reason + CRLF;
        headers.append(responseLine);
        for (Map.Entry<String, String> header : headers().entrySet()) {
            headers.append(header.getKey()).append(": ").append(header.getValue()).append(CRLF);
        }
        headers.append(CRLF);
        headers.append(new String(body));
        return headers.toString();
    }
}
