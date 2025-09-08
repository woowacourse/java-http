package org.apache.coyote.http11.resource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Optional;
import org.apache.coyote.http11.dispatcher.handlerAdapter.ViewResolver;

public class ResourceUtil {

    private ResourceUtil() {}

    public static Optional<URL> find(String resourcePath) {
        URL url = ViewResolver.resolve(resourcePath);
        if (url == null) {
            return Optional.empty();
        }
        return Optional.of(url);
    }

    public static byte[] readAll(URL resource) throws IOException {
        try (InputStream inputStream = resource.openStream()) {
            return inputStream.readAllBytes();
        }
    }
}
