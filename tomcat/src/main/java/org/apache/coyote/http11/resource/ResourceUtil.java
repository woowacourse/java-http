package org.apache.coyote.http11.resource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Optional;

public class ResourceUtil {

    private static final String[] CLASSPATH_LOCATION = {
            "static", "static/css"
    };

    private ResourceUtil() {}

    public static Optional<URL> find(String url) {
        for (String classPath : CLASSPATH_LOCATION) {
            String resourcePath = String.join("/", classPath, url);
            URL resource = ResourceUtil.class.getClassLoader().getResource(resourcePath);

            if (resource != null) {
                return Optional.of(resource);
            }
        }
        return Optional.empty();
    }

    public static byte[] readAll(URL resource) throws IOException {
        try (InputStream inputStream = resource.openStream()) {
            return inputStream.readAllBytes();
        }
    }
}
