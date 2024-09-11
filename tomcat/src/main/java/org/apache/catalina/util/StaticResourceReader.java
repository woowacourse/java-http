package org.apache.catalina.util;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;
import org.apache.coyote.http11.response.ResponseFile;

public class StaticResourceReader {

    private static final String STATIC_RESOURCE_PREFIX = "static";

    private StaticResourceReader() {
    }

    public static ResponseFile read(String filePath) {
        URL resource = getResource(filePath);
        if (!isExist(filePath)) {
            throw new IllegalArgumentException("Resource not found: " + filePath);
        }
        return ResponseFile.of(resource);
    }

    public static boolean isExist(String filePath) {
        URL resource = getResource(filePath);
        try {
            File file = new File(Objects.requireNonNull(resource).toURI());
            return file.exists() && file.isFile();
        } catch (URISyntaxException | NullPointerException e) {
            return false;
        }
    }

    private static URL getResource(String filePath) {
        return ClassLoader.getSystemClassLoader().getResource(STATIC_RESOURCE_PREFIX + filePath);
    }
}
