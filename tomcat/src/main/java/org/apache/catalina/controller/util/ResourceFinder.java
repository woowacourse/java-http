package org.apache.catalina.controller.util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.catalina.controller.StaticResourceController;

public class ResourceFinder {

    public static final String INDEX_RESOURCE_PATH = "/index.html";
    public static final String UNAUTHORIZED_RESOURCE_PATH = "/401.html";

    private static final String STATIC_RESOURCE_PATH = "static";

    public static String findResource(final String requestPath) {
        URL resourceUrl = StaticResourceController.class.getClassLoader().getResource(STATIC_RESOURCE_PATH + requestPath);

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
