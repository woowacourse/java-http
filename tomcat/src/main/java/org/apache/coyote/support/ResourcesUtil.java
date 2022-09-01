package org.apache.coyote.support;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import org.apache.coyote.exception.ResourceNotFoundException;

public class ResourcesUtil {

    private static final String EXTENSION_DELIMITER = ".";

    private ResourcesUtil() {
    }

    public static String parseExtension(final String uri) {
        if (uri.contains(EXTENSION_DELIMITER)) {
            return uri.substring(uri.lastIndexOf(EXTENSION_DELIMITER) + 1);
        }
        return "";
    }

    public static String readResource(final String resourcePath) throws IOException {
        URL resource = Thread.currentThread().getContextClassLoader().getResource(resourcePath);
        try {
            Objects.requireNonNull(resource);
        } catch (NullPointerException e) {
            throw new ResourceNotFoundException(resourcePath);
        }
        Path path = new File(resource.getPath()).toPath();
        return new String(Files.readAllBytes(path));
    }
}
