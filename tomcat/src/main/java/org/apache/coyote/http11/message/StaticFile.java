package org.apache.coyote.http11.message;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.apache.catalina.exception.file.FileReadException;
import org.apache.catalina.exception.file.NoExistFileException;
import org.apache.catalina.exception.file.NoExistFileExtensionException;

public class StaticFile {

    private static final String STATIC_ROOT_PATH = "static/";
    private static final char EXTENSION_DELIMITER = '.';

    private final String path;
    private final String extension;

    public StaticFile(final String path, final String extension) {
        this.path = path;
        this.extension = extension;
    }

    public StaticFile(final String path) {
        this(path, parseExtension(path));
    }

    public String getBody() {
        final ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        final URL url = classLoader.getResource(STATIC_ROOT_PATH + path);
        if (url == null) {
            throw new NoExistFileException();
        }
        try {
            return new String(Files.readAllBytes(new File(url.getFile()).toPath()));
        } catch (final IOException e) {
            throw new FileReadException();
        }
    }

    private static String parseExtension(final String path) {
        final int lastDotIndex = path.lastIndexOf(EXTENSION_DELIMITER);
        if (lastDotIndex > 0) {
            return path.substring(lastDotIndex);
        }
        throw new NoExistFileExtensionException();
    }

    public String getExtension() {
        return extension;
    }
}
