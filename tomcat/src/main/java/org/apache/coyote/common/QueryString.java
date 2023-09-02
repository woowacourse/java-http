package org.apache.coyote.common;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class QueryString {

    public static final QueryString EMPTY = new QueryString(Collections.emptyMap());

    private final Map<String, List<String>> queries;

    public QueryString(Map<String, List<String>> queries) {
        this.queries = queries;
    }

    public String get(String key) {
        List<String> query = this.queries.getOrDefault(key, Collections.emptyList());
        return String.join(",", query);
    }

    public Map<String, List<String>> getQueries() {
        return Collections.unmodifiableMap(queries);
    }
}
