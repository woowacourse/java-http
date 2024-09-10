package org.apache.coyote.http11;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

class ResourceFinder {

    private static final String RESOURCE_PATH_FORMAT = "static/%s";

    public static boolean hasResource(String resourceName, ClassLoader classLoader) {
        URL resourcePath = classLoader.getResource(String.format(RESOURCE_PATH_FORMAT, resourceName));

        return resourcePath != null && !"/".equals(resourceName);
    }

    public static byte[] readResource(String resourceName, ClassLoader classLoader) {
        URL resourcePath = classLoader.getResource(String.format(RESOURCE_PATH_FORMAT, resourceName));
        if (resourcePath == null) {
            throw new RuntimeException();
        }

        try (InputStream inputStream = new FileInputStream(resourcePath.getPath())) {
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            return bufferedInputStream.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
