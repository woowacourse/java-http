package org.apache.coyote.http11;

import java.nio.charset.StandardCharsets;

public record HttpResponse(byte[] responseBody, String contentType) {

    public byte[] serialize() {
        String message = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType + " ",
                "Content-Length: " + responseBody.length + " ",
                "",
                new String(responseBody, StandardCharsets.UTF_8));

        return message.getBytes();
    }
}
