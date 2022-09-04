package org.apache.coyote.util;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileUtils {

    private FileUtils() {

    }

    public static String readAllBytes(final String uri) {
        URL resource = Thread.currentThread().getContextClassLoader().getResource("static" + uri);
        if (resource != null) {
            String file = resource.getFile();
            return read(file);
        }
        throw new RuntimeException();
    }

    private static String read(final String file) {
        try {
            return new String(Files.readAllBytes(Paths.get(file)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
