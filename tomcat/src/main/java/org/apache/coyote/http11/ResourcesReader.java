package org.apache.coyote.http11;

import java.io.IOException;
import java.io.InputStream;

public class ResourcesReader {
    private static final String DEFAULT_PATH = "static";
    private static final String MAIN_PAGE = "/main.html";

    public Resource read(final String path) throws IOException {
        final String p = path.equals("/") ? MAIN_PAGE : path;
        final String resourcePath = DEFAULT_PATH + p;

        try (final InputStream inputStream = ClassLoaderContext.getResourceAsStream(resourcePath)) {
            final byte[] bytes = inputStream.readAllBytes();
            return new Resource(resourcePath, bytes);
        }
    }
}
