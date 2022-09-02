package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class ResourceLoader {

    private static final String STATIC_RESOURCE_LOCATION = "static/";
    private static final String DEFAULT_RESPONSE_BODY = "Hello world!";
    private static final String DEFAULT_URI = "/";

    private ResourceLoader() {
    }

    public static String getContent(final String uri) throws IOException {
        if (uri.equals(DEFAULT_URI)) {
            return DEFAULT_RESPONSE_BODY;
        }

        final URL resource = ResourceLoader.class.getClassLoader().getResource(STATIC_RESOURCE_LOCATION + uri);
        final Path path = new File(Objects.requireNonNull(resource).getPath()).toPath();
        final byte[] bytes = Files.readAllBytes(path);

        return new String(bytes);
    }
}
