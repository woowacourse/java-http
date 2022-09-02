package org.apache.coyote.http11.util;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import org.apache.coyote.http11.exception.NotFoundFileException;

public class FileLoader {

    private static final String STATIC_DIRECTORY = "static";

    private FileLoader() {
    }

    public static String loadFile(final String fileLocation) throws IOException {
        final URL url = ClassLoader.getSystemResource(STATIC_DIRECTORY + fileLocation);
        if (Objects.isNull(url)) {
            throw new NotFoundFileException();
        }
        final Path path = Paths.get(url.getPath());
        return new String(Files.readAllBytes(path));
    }
}
