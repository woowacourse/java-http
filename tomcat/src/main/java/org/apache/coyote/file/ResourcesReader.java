package org.apache.coyote.file;

import org.apache.coyote.http11.Path;

import java.io.IOException;
import java.io.InputStream;

public class ResourcesReader {
    private static final String DEFAULT_PATH = "static";
    private static final String MAIN_PAGE = "/main.html";

    public static Resource read(final Path path){
        return read(path.value());
    }

    public static Resource read(final String path) {
        final String p = path.equals("/") ? MAIN_PAGE : path;
        final String resourcePath = DEFAULT_PATH + p;

        try (final InputStream inputStream = ClassLoaderContext.getResourceAsStream(resourcePath)) {
            final byte[] bytes = inputStream.readAllBytes();
            return new Resource(resourcePath, bytes);
        } catch (final IOException e) {
            throw new FileNotFoundException(String.format("%s not found", resourcePath),e);
        }
    }
    private ResourcesReader() {}
}
