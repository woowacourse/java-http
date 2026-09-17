package org.apache.catalina.resource;

public class ResourcePathResolver {
    private static final String DEFAULT_PAGE = "index.html";
    private static final String HTML_EXTENSION = ".html";

    private ResourcePathResolver() {
    }

    public static String resolve(String path) {
        if (path.endsWith("/")) {
            return path + DEFAULT_PAGE;
        }
        if (hasExtension(path)) {
            return path;
        }
        return path + HTML_EXTENSION;
    }

    private static boolean hasExtension(String path) {
        final String lastSegment = path.substring(path.lastIndexOf('/') + 1);
        return lastSegment.contains(".");
    }
}
