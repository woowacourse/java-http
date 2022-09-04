package nextstep.jwp.http.reqeust;

import java.util.HashMap;
import java.util.Map;

public class QueryParams {

    private static final String QUERY_CONNECTOR = "&";
    private static final String QUERY_SEPARATOR = "=";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> value;

    public QueryParams(final String queryString) {
        Map<String, String> queryParams = new HashMap<>();

        if (queryString != null) {
            insertQuery(queryParams, queryString);
        }
        this.value = queryParams;
    }

    private void insertQuery(final Map<String, String> queryParams, final String queryString) {
        String[] queries = queryString.split(QUERY_CONNECTOR);
        for (String query : queries) {
            String[] queryParam = query.split(QUERY_SEPARATOR);
            queryParams.put(queryParam[KEY_INDEX], queryParam[VALUE_INDEX]);
        }
    }

    public boolean isNotEmpty() {
        return !value.isEmpty();
    }

    public Map<String, String> getParams() {
        return value;
    }
}
