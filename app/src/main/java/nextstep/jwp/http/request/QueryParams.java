package nextstep.jwp.http.request;

import static java.util.stream.Collectors.toMap;

import java.util.Arrays;
import java.util.Collections;
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
        return Arrays.stream(queryString.split("&"))
                .map(line -> line.split("="))
                .filter(pair -> pair.length == 2)
                .collect(toMap(pair -> pair[0], pair -> pair[1]));
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

    @Override
    public String toString() {
        return "QueryParams{" +
                "params=" + params +
                '}';
    }
}
