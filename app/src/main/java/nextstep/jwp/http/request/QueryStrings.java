package nextstep.jwp.http.request;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class QueryStrings {

    private Map<String, String> queryStrings = new HashMap<>();

    public QueryStrings() {
    }

    public QueryStrings(Map<String, String> queryStrings) {
        this.queryStrings = new HashMap<>(queryStrings);
    }

    public Map<String, String> getAllQueryStrings() {
        return Collections.unmodifiableMap(queryStrings);
    }

    public boolean isEmpty() {
        return this.queryStrings.isEmpty();
    }

    public String getValue(String key) {
        return this.queryStrings.get(key);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        QueryStrings that = (QueryStrings) o;
        return Objects.equals(queryStrings, that.queryStrings);
    }

    @Override
    public int hashCode() {
        return Objects.hash(queryStrings);
    }
}
