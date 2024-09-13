package org.apache.coyote.http.request.uri;

public class Path {

    private static final String PATH_PREFIX = "/";

    private final String path;

    protected Path(String path) {
        validatePath(path);
        this.path = path;
    }

    private void validatePath(String path) {
        if (path == null || path.isBlank()) {
            throw new IllegalArgumentException("Path는 필수입니다.");
        }
        if (!path.startsWith(PATH_PREFIX)) {
            throw new IllegalArgumentException("Path는 %s로 시작해야 합니다.".formatted(PATH_PREFIX));
        }
    }

    protected String getPath() {
        return this.path;
    }
}
