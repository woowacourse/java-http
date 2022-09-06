package org.apache.coyote.http11.request.element;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class Query {

    private static final String PATH_DELIMITER = "?";
    private static final String QUERY_DELIMITER = "&";
    private static final String PARAM_DELIMITER = "=";

    private final Map<String, String> params;

    public Query(Map<String, String> params) {
        this.params = params;
    }

    public Query(String uri) {
        this.params = extractQuery(uri);
    }

    public static Query ofUri(String uri) {
        return new Query(extractQuery(uri));
    }

    public static Query ofQuery(String query) {
        return new Query(parse(query));
    }

    private static Map<String, String> extractQuery(String uri) {
        if (!uri.contains(PATH_DELIMITER)) {
            return new HashMap<>();
        }
        String query = uri.substring(uri.indexOf(PATH_DELIMITER) + 1);
        return parse(query);
    }

    private static Map<String, String> parse(String query) {
        String[] params = query
                .split(QUERY_DELIMITER);

        return Arrays.stream(params)
                .map(element -> element.split(PARAM_DELIMITER))
                .collect(Collectors.toMap(split -> split[0], split -> split[1]));
    }

    public String find(String param) {
        return params.get(param);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Query query = (Query) o;
        return Objects.equals(params, query.params);
    }

    @Override
    public int hashCode() {
        return Objects.hash(params);
    }
}
