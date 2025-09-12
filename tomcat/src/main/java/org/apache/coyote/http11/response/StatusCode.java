package org.apache.coyote.http11.response;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public enum StatusCode {

    OK("HTTP/1.1 200 OK "),
    REDIRECT("HTTP/1.1 302 Found"),
    NOT_FOUND("HTTP/1.1 404 Not Found");

    private final String message;

    StatusCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public String getResponse(Path path) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();

        if (this == OK) {
            stringBuilder.append(message);
            stringBuilder.append("Content-Type: ").append(Files.probeContentType(path)).append(";charset=utf-8 ");
            stringBuilder.append("Content-Length: ").append(Files.size(path)).append(" ");
            stringBuilder.append(" ").append(" ");
            return stringBuilder.toString();
        }

        if (this == REDIRECT) {
            stringBuilder.append(message);
            stringBuilder.append("Location: ").append(path.toString()).append(" ");
            stringBuilder.append("Content-Length: 0\r\n");
            stringBuilder.append("\r\n");
            return stringBuilder.toString();
        }

        return String.join("\r\n",
                "HTTP/1.1 404 Not Found",
                "Content-Type: text/plain; charset=utf-8",
                "Content-Length: 0"
        );
    }
}
