package org.apache.coyote.http11.common;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

public class FileReader {
    private static final String BASE_DIRECTORY = "static";

    private FileReader() {
    }

    public static String read(final String fileName) throws IOException {
        try {
            final ClassLoader classLoader = ClassLoader.getSystemClassLoader();
            URL url = classLoader.getResource(BASE_DIRECTORY + fileName);
            final File file = new File(url.getFile());
            return new String(Files.readAllBytes(file.toPath()));
        } catch(NullPointerException exception) {
            throw new IOException();
        }
    }
}
