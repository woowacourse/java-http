package org.apache.coyote.http11.handler;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class StaticResourceHandler {

    public static String readFile(final String path) throws IOException {
        final String filePath = String.format("static%s", path);
        final URL resource = Thread.currentThread().getContextClassLoader().getResource(filePath);
        return Files.readString(Path.of(Objects.requireNonNull(resource).getPath()));
    }
}
