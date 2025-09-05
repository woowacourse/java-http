package org.apache.coyote.util;

import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import org.apache.exception.StaticResourceNotFoundException;

public class ResourceUtil {

    public static boolean isStaticResourceExist(final String path, final Class<?> classLoader) {
        final String STATIC_PATH = "/static";

        try {
            URL url = Objects.requireNonNull(
                    classLoader.getResource(STATIC_PATH + path)
            );
            URI uri = url.toURI();
            Path filePath = Paths.get(uri);

            return Files.isRegularFile(filePath);
        } catch (Exception e) {
            return false;
        }
    }

    public static String readStaticResource(final String path, final Class<?> classLoader) {
        final String STATIC_PATH = "/static";

        try {
            URL url = Objects.requireNonNull(
                    classLoader.getResource(STATIC_PATH + path)
            );
            URI uri = url.toURI();
            Path filePath = Paths.get(uri);

            return Files.readString(filePath);
        } catch (Exception e) {
            throw new StaticResourceNotFoundException(path + ": file doesn't exist");
        }
    }
}
