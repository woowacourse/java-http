package org.apache.coyote.http11.common;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

public class ResourceReader {

    private static final ClassLoader CLASS_LOADER = ClassLoader.getSystemClassLoader();

    public static URL getResourceUrl(final String resourceName) {
        return CLASS_LOADER.getResource("static" + resourceName);
    }

    public static String read(final URL resource) throws IOException {
        final byte[] bytes = Files.readAllBytes(new File(resource.getFile()).toPath());
        return new String(bytes);
    }
}
