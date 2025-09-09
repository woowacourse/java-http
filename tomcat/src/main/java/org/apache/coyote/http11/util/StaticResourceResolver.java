package org.apache.coyote.http11.util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

public final class StaticResourceResolver {

    private static final String STATIC_DIRECTORY = "static/";

    private StaticResourceResolver() {}

    public static URL findResource(String path) {
        if (path.startsWith("/")) {
            path = path.substring(1);
        }

        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        URL resource = cl.getResource(STATIC_DIRECTORY + path);
        if (resource == null) {
            resource = cl.getResource(STATIC_DIRECTORY + path + ".html");
        }
        return resource;
    }

    public static String readAsString(String path) throws IOException {
        URL resource = findResource(path);
        if (resource == null) {
            return null;
        }
        try {
            return Files.readString(Paths.get(resource.toURI()));
        } catch (URISyntaxException e) {
            throw new IOException("유효하지 않은 resource URI: " + path, e);
        }
    }
}
