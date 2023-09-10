package org.apache.catalina;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileLoader {

    private FileLoader() {
    }

    public static String load(final String resourcePath) throws IOException {
        final ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        final URL resource = classLoader.getResource(resourcePath);

        if (resource == null) {
            return null;
        }

        final File file = new File(resource.getFile());
        final Path path = file.toPath();

        return new String(Files.readAllBytes(path));
    }
}
