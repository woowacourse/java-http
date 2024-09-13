package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

public class FileFinder {

    public Optional<String> readFileContent(final String fileName) throws URISyntaxException {
        final Optional<File> file = getStaticFile(fileName);
        return file.map(data -> {
            try {
                return new String(Files.readAllBytes(data.toPath()));
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        });
    }

    public Optional<File> getStaticFile(final String fileName) throws URISyntaxException {
        final URL resource = getClass().getResource("/static" + fileName.trim());
        if (resource == null) {
            return Optional.empty();
        }

        return Optional.of(Paths.get(resource.toURI()).toFile());
    }
}
