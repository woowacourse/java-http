package org.apache.coyote.http11.common;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileReader {

    private FileReader() {
    }

    public static String read(final String resource) {
        try {
            final Path path = Paths.get(ClassLoader
                    .getSystemResource("static" + resource)
                    .getFile()
            );

            return new String(Files.readAllBytes(path));
        } catch (IOException e) {
            return "";
        }
    }
}
