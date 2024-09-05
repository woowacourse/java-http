package org.apache.coyote.http.request;

public class Path {

    private final String path;

    public Path(String path) {
        validatePath(path);
        this.path = path;
    }

    private void validatePath(String path) {
        if (path == null || path.isEmpty()) {
            throw new IllegalArgumentException("Path cannot be null or empty");
        }
        if (!path.startsWith("/")) {
            throw new IllegalArgumentException("Path must start with '/'");
        }
    }

    public String getPath() {
        return path;
    }
}
