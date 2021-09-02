package nextstep.jwp.http.request;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class QueryParams {
    private final Map<String, String> params;

    public QueryParams(Map<String, String> params) {
        this.params = params;
    }

    public static QueryParams of(String queryString) {
        try {
            Map<String, String> params = parse(queryString);
            return new QueryParams(params);
        } catch (Exception e) {
            return new QueryParams(Collections.emptyMap());
        }
    }

    private static Map<String, String> parse(String queryString) {
        Map<String, String> params = new HashMap<>();
        for (String pair : queryString.split("&")) {
            String[] keyValue = pair.split("=");
            params.put(keyValue[0], keyValue[1]);
        }
        return params;
    }

    public String get(String key) {
        return params.get(key);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        QueryParams that = (QueryParams) o;
        return Objects.equals(params, that.params);
    }

    @Override
    public int hashCode() {
        return Objects.hash(params);
    }
}
