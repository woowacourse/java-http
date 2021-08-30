package nextstep.jwp.http.request;

public class URI {

    private static final String PATH_PREFIX = "/";
    private final String path;

    private final QueryStrings queryStrings;

    public URI(String path, QueryStrings queryStrings) {
        validatePath(path);
        this.path = path;
        this.queryStrings = queryStrings;
    }

    private void validatePath(String path) {
        if (path == null || path.isBlank() || !path.startsWith(PATH_PREFIX)) {
            throw new IllegalArgumentException();
        }
    }

    public QueryStrings getQueryStrings() {
        return queryStrings;
    }

    public String getPath() {
        return path;
    }
}
