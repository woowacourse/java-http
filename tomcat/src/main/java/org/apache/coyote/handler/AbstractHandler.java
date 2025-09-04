package org.apache.coyote.handler;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public abstract class AbstractHandler {

    public abstract boolean canHandle(final String requestTarget);

    public abstract String handle(final String requestTarget) throws IOException;

    protected String getStaticResponseBody(final String requestTarget) throws IOException {
        final URL resource = getClass().getClassLoader().getResource("static" + requestTarget);
        if (resource == null) {
            return null;
        }
        final Path path = Paths.get(resource.getPath());
        final List<String> contents = Files.readAllLines(path);

        return String.join("\r\n", contents);
    }

    protected static String createResponse(
            final String responseBody,
            final String contentType
    ) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType + " ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
