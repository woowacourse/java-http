package nextstep.jwp.http.reqeust;

import java.util.HashMap;
import java.util.Map;

public class QueryParams {

    private static final String QUERY_IDENTIFIER = "?";
    private static final String QUERY_CONNECTOR = "&";
    private static final String QUERY_SEPARATOR = "=";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> queryParams;

    public QueryParams(final String url) {
        queryParams = new HashMap<>();

        if (url.contains(QUERY_IDENTIFIER)) {
            int index = url.indexOf(QUERY_IDENTIFIER);
            String[] queryStrings = url.substring(index + 1).split(QUERY_CONNECTOR);
            initQueryParams(queryStrings);
        }
    }

    private void initQueryParams(String[] queryStrings) {
        for (String queryString : queryStrings) {
            String[] queryParam = queryString.split(QUERY_SEPARATOR);
            this.queryParams.put(queryParam[KEY_INDEX], queryParam[VALUE_INDEX]);
        }
    }

    public boolean isNotEmpty() {
        return !queryParams.isEmpty();
    }

    public Map<String, String> getParams() {
        return queryParams;
    }
}
