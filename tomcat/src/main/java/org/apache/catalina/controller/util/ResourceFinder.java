package org.apache.catalina.controller.util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.catalina.controller.StaticResourceController;

public class ResourceFinder {

    private static final String STATIC_RECOURSE_PATH = "static";

    public static String findResource(final String requestPath) {
        URL resourceUrl = StaticResourceController.class.getClassLoader().getResource(STATIC_RECOURSE_PATH + requestPath);

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
