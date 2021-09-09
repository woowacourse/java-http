package nextstep.jwp.http.entity;

import java.util.Objects;

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

    public boolean endsWith(String suffix) {
        return path.endsWith(suffix);
    }

    public boolean hasPathOf(String path) {
        return this.path.equals(path);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        HttpUri httpUri = (HttpUri) o;
        return Objects.equals(path, httpUri.path) && Objects.equals(queryString, httpUri.queryString);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path, queryString);
    }
}
