package org.apache.coyote.support;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.exception.NotFoundException;

public class ResourceResponse {

    private final Path path;

    public ResourceResponse(String uri) {
        this.path = toResourcePath(uri);
    }

    private Path toResourcePath(String uri) {
        try {
            File file = new File(findUrl(uri).getFile());
            return file.toPath();
        } catch (NullPointerException e) {
            throw new NotFoundException();
        }
    }

    private URL findUrl(String uri) {
        final var classLoader = getClass().getClassLoader();
        if (uri.equals("/")) {
            return classLoader.getResource("static/index.html");
        }
        return classLoader.getResource("static" + uri);
    }

    public String toContent() throws IOException {
        return new String(Files.readAllBytes(path));
    }

    public String toContentType() throws IOException {
        return Files.probeContentType(path);
    }
}
