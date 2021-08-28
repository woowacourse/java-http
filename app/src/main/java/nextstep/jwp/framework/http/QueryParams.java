package nextstep.jwp.framework.http;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class QueryParams {

    private static final String QUERY_PARAMETER_REGEX = "&";
    private static final String QUERY_PARAMETER_KEY_VALUE_REGEX = "=";
    private static final int KEY = 0;
    private static final int VALUE = 1;

    private final Map<String, String> queryParams;

    public QueryParams() {
        this.queryParams = new HashMap<>();
    }

    public QueryParams(String query) {
        this.queryParams = createQueryParams(query);
    }

    private Map<String, String> createQueryParams(String query) {
        final Map<String, String> queryParams = new HashMap<>();
        final String[] params = query.split(QUERY_PARAMETER_REGEX);

        for (String param : params) {
            final String[] element = param.split(QUERY_PARAMETER_KEY_VALUE_REGEX);
            queryParams.put(element[KEY], element[VALUE]);
        }

        return queryParams;
    }

    public int count() {
        return queryParams.size();
    }

    public Map<String, String> getQueryParams() {
        return Collections.unmodifiableMap(queryParams);
    }
}
