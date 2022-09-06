package nextstep.jwp.http.reqeust;

import java.util.HashMap;
import java.util.Map;

public class QueryParams {

    private static final String QUERY_CONNECTOR = "&";
    private static final String QUERY_SEPARATOR = "=";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> values;

    public QueryParams(final String queryString) {
        this.values = new HashMap<>();

        if (queryString != null) {
            addValueFrom(queryString);
        }
    }

    private void addValueFrom(final String queryString) {
        String[] queries = queryString.split(QUERY_CONNECTOR);

        for (String query : queries) {
            String[] value = query.split(QUERY_SEPARATOR);
            this.values.put(value[KEY_INDEX], value[VALUE_INDEX]);
        }
    }

    public boolean isNotEmpty() {
        return !values.isEmpty();
    }

    public Map<String, String> getValues() {
        return values;
    }
}
