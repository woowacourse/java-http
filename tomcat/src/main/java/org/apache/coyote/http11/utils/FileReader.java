package org.apache.coyote.http11.utils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.coyote.http11.exception.NoSuchFileException;

public class FileReader {

    private static final String STATIC_FILE_PATH = "static";

    public static String read(final String filePath) {
        final URL url = FileReader.class.getClassLoader().getResource(STATIC_FILE_PATH + filePath);
        try {
            final Path path = new File(url.getFile()).toPath();
            return Files.readString(path);
        } catch (IOException | NullPointerException e) {
            throw new NoSuchFileException(e.getMessage());
        }
    }
}
