package org.apache.coyote.http11;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;

public class Http11ResourceFinder {
    public static final String INDEX_HTML = "/index.html";
    public static final String NOT_FOUND_HTML = "/404.html";

    private final ClassLoader classLoader;

    public Http11ResourceFinder() {
        this.classLoader = getClass().getClassLoader();
    }

    public Path find(String requestUri) {
        String relativePath = toRelativePath(requestUri);
        URL resource = classLoader.getResource(relativePath);
        try {
            return Path.of(resource.toURI());
        } catch (URISyntaxException | NullPointerException e) {
            return find(NOT_FOUND_HTML);
        }
    }

    private String toRelativePath(String requestUri) {
        String replaceFirstSlash = requestUri.replaceFirst("/", "");

        if (replaceFirstSlash.isEmpty()) {
            return toRelativePath(INDEX_HTML);
        }

        if (!replaceFirstSlash.contains(".")) {
            replaceFirstSlash = replaceFirstSlash + ".html";
        }
        return "static" + File.separator + replaceFirstSlash;
    }
}
