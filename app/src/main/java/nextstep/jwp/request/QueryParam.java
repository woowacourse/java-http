package nextstep.jwp.request;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class QueryParam {
    private static final String QUERY_PARAM_SEPARATOR = "&";
    private static final String KEY_VALUE_SEPARATOR = "=";

    private final Map<String, String> queryParams;

    public QueryParam(Map<String, String> queryParams) {
        this.queryParams = queryParams;
    }

    public static QueryParam of(String queryString) {
        final Map<String, String> requestQueryString = new HashMap<>();
        for (String query : queryString.split(QUERY_PARAM_SEPARATOR)) {
            final String[] queriedValue = query.split(KEY_VALUE_SEPARATOR);
            requestQueryString.put(queriedValue[0], queriedValue[1]);
        }
        return new QueryParam(requestQueryString);
    }

    public String searchValue(String key) {
        if (queryParams.containsKey(key)) {
            return queryParams.get(key);
        }
        return "";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QueryParam that = (QueryParam) o;
        return Objects.equals(queryParams, that.queryParams);
    }

    @Override
    public int hashCode() {
        return Objects.hash(queryParams);
    }
}
