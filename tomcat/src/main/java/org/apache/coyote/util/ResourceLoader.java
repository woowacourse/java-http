package org.apache.coyote.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

public class ResourceLoader {

    public static byte[] get(final String uri) throws IOException {
        URL url = getUrl(uri);

        if (url == null) {
            return null;
        }

        final var targetFile = url.getFile();
        return Files.readAllBytes(new File(targetFile).toPath());
    }

    private static URL getUrl(final String uri) {
        return ResourceLoader.class.getClassLoader().getResource("static" + uri);
    }
}
