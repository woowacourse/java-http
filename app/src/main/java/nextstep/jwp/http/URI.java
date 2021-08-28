package nextstep.jwp.http;

import java.util.Map;
import java.util.Objects;

public class URI {

    private final String path;

    private final QueryStrings queryStrings;

    public String getPath() {
        return path;
    }

    public QueryStrings getQueryStrings() {
        return queryStrings;
    }

    public URI(String path, QueryStrings queryStrings) {
        this.path = path;
        this.queryStrings = queryStrings;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        URI uri = (URI) o;
        return path.equals(uri.path) && Objects.equals(queryStrings, uri.queryStrings);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path, queryStrings);
    }
}
