package org.apache.util;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import org.apache.request.RequestUri;

public class StaticResourceFinder {

    public static boolean isStaticResourceExist(final RequestUri requestUri) {
        try {
            Optional<String> staticResource = findStaticResource(requestUri);
            return staticResource.isPresent();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Optional<String> findStaticResource(final RequestUri requestUri) throws IOException {
        try {
            URL resourceUrl = StaticResourceFinder.class
                    .getClassLoader()
                    .getResource("static" + requestUri.getValue());
            if (resourceUrl == null) {
                return Optional.empty();
            }
            Path resourcePath = Paths.get(resourceUrl.getPath());
            return Optional.of(Files.readString(resourcePath));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
