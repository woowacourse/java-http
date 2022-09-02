package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public interface Handler {

    String handle(final HttpRequest httpRequest) throws IOException;

    default String createResponseBody(final String resourcePath) throws IOException {
        URL resource = getClass()
                .getClassLoader()
                .getResource("static" + resourcePath);
        String filePath = Objects.requireNonNull(resource)
                .getFile();
        Path path = new File(filePath).toPath();
        return new String(Files.readAllBytes(path));
    }

    default String createResponseMessage(final ContentType contentType, final String responseBody) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType.getValue() + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
