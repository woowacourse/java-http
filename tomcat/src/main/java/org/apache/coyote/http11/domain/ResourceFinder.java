package org.apache.coyote.http11.domain;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.coyote.http11.request.domain.RequestPath;

public final class ResourceFinder {

    private ResourceFinder() {
    }

    public static String find(RequestPath requestPath) {
        URL resourceUrl = ResourceFinder.class.getClassLoader().getResource(requestPath.toResourcePath());

        try {
            Path filePath = Path.of(resourceUrl.toURI());

            return Files.readString(filePath);
        } catch (URISyntaxException | IOException e) {
            throw new IllegalArgumentException(e);
        } catch (NullPointerException e) {
            throw new NullPointerException(e.getMessage());
        }
    }
}
