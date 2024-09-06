package servlet.http.request.uri;

public class PathVariable {

    private final String path;

    public PathVariable(String path) {
        this.path = path;
    }

    public String getPath() {
        return this.path;
    }
}
