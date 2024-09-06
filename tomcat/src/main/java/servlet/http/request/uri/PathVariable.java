package servlet.http.request.uri;

public class PathVariable {

    private final String path;

    protected PathVariable(String path) {
        this.path = path;
    }

    protected String getPath() {
        return this.path;
    }
}
