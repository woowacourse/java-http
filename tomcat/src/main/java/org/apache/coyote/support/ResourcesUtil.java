package org.apache.coyote.support;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class ResourcesUtil {

    private ResourcesUtil() {
    }

    public static String readResource(final String resourcePath) throws IOException {
        URL resource = Thread.currentThread().getContextClassLoader().getResource(resourcePath);
        assert resource != null;
        Path path = new File(resource.getPath()).toPath();
        return new String(Files.readAllBytes(path));
    }
}
