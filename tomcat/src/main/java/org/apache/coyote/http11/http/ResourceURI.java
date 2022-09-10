package org.apache.coyote.http11.http;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.coyote.http11.util.PathParser;

public class ResourceURI {

    private static final String SPACE = " ";

    private final String viewName;

    private ResourceURI(final String viewName) {
        this.viewName = viewName;
    }

    public static ResourceURI from(final String viewName) {
        return new ResourceURI(viewName);
    }

    public String getContent() throws IOException {
        final var absolutePath = PathParser.parsePath(viewName);
        final var contentType = Files.probeContentType(absolutePath);
        final var responseBody = readFile(absolutePath);

        return String.join(System.lineSeparator(),
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + SPACE,
                "",
                responseBody);
    }

    private String readFile(final Path filePath) throws IOException {
        return String.join("\n", Files.readAllLines(filePath)) + "\n";
    }
}
