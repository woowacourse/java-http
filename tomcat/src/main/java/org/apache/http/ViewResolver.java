package org.apache.http;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;

public final class ViewResolver {

    private static final String DOT = ".";
    private static final String HTML_FILE_EXTENSION = ".html";

    private final Path resourcePath;

    public ViewResolver(final Path resourcePath) {
        final Path path = Path.of(htmlResourceResolve(resourcePath.toString()));
        final URL resource = getClass().getClassLoader().getResource("static" + path);
        this.resourcePath = new File(resource.getPath()).toPath();
    }

    private String htmlResourceResolve(final String resourcePath) {
        if (!resourcePath.contains(DOT) && !resourcePath.endsWith(HTML_FILE_EXTENSION)) {
            return resourcePath + HTML_FILE_EXTENSION;
        }

        return resourcePath;
    }

    public Path getResourcePath() {
        return resourcePath;
    }

    public String getFileExtension() {
        final int lastDotIndex = resourcePath.toString().lastIndexOf(DOT);
        return resourcePath.toString().substring(lastDotIndex + 1);
    }
}
