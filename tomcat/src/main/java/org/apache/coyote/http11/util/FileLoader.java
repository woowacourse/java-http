package org.apache.coyote.http11.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Objects;
import org.apache.coyote.http11.exception.notfound.NotFoundFileException;

public class FileLoader {

    private static final String STATIC_DIRECTORY = "static";

    private FileLoader() {
    }

    public static String loadFile(final String fileLocation) throws IOException {
        final URL url = Thread.currentThread().getContextClassLoader().getResource(STATIC_DIRECTORY + fileLocation);
        if (Objects.isNull(url)) {
            throw new NotFoundFileException();
        }
        final File file = new File(url.getFile());
        return new String(Files.readAllBytes(file.toPath()));
    }
}
