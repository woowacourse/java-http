package org.apache.coyote.http11;

import java.nio.charset.StandardCharsets;

public record HttpResponse(String contentType, String responseBody) {

    public String serveResponse() {
        return String.join("\r\n",
            "HTTP/1.1 200 OK ",
            "Content-Type: text/" + contentType + ";charset=utf-8 ",
            "Content-Length: " + responseBody.getBytes(StandardCharsets.UTF_8).length + " ",
            "",
            responseBody);
    }
}
