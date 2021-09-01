package nextstep.jwp.http.entity;

public class HttpUri {
    private final String path;
    private final String queryString;

    private HttpUri(String path) {
        this(path, "");
    }

    private HttpUri(String path, String queryString) {
        this.path = path;
        this.queryString = queryString;
    }

    public static HttpUri of(String uri) {
        if (uri.contains("?")) {
            String[] split = uri.split("\\?", 2);
            return new HttpUri(split[0], split[1]);
        }
        return new HttpUri(uri);
    }

    public String path() {
        return path;
    }

    public String queryString() {
        return queryString;
    }
}
