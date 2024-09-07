package servlet.http.request.uri;

public class Path {

    private final String path;

    protected Path(String path) {
        if (path == null || path.isBlank()) {
            throw new IllegalArgumentException("Path가 존재하지 않습니다.");
        }
        this.path = path;
    }

    protected String getPath() {
        return this.path;
    }
}
