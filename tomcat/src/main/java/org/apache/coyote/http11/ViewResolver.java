package org.apache.coyote.http11;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ViewResolver {

    private final String filePath;

    public ViewResolver(final String filePath) {
        this.filePath = filePath;
    }

    public ViewInfo render() throws IOException, URISyntaxException {
        final URI uri = getClass().getClassLoader().getResource("static/" + filePath).toURI();
        final Path path = Paths.get(uri);
        final byte[] bytes = Files.readAllBytes(path);
        final String contentType = Files.probeContentType(path);

        return new ViewInfo(new String(bytes), contentType, bytes.length);
    }
}
