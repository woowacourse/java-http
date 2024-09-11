package servlet.http.request.uri;

public class Path {

    private final String path;

    protected Path(String path) {
        validatePath(path);
        this.path = path;
    }

    private void validatePath(String path) {
        if (path == null || path.isBlank()) {
            throw new IllegalArgumentException("Path는 필수입니다.");
        }
    }

    protected String getPath() {
        return this.path;
    }
}
