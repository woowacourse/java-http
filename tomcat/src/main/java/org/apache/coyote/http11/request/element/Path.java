package org.apache.coyote.http11.request.element;

public class Path {

    private static final String PATH_REGEX = "\\?";

    private final String path;

    private Path(String path) {
        this.path = path;
    }

    public static Path of(String uri) {
        return new Path(uri.split(PATH_REGEX)[0]);
    }

    public String getPath() {
        return path;
    }
}
