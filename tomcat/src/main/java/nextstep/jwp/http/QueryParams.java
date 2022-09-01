package nextstep.jwp.http;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class QueryParams {

    private static final String QUERY_PARAM_SEPARATOR = "&";
    private static final String QUERY_PARAM_KEY_VALUE_SEPARATOR = "=";
    private static final int RAW_QUERY_PARAM_KEY_INDEX = 0;
    private static final int RAW_QUERY_PARAM_VALUE_INDEX = 1;
    private final Map<String, String> queryParams;

    private QueryParams(final Map<String, String> queryParams) {
        this.queryParams = queryParams;
    }

    public static QueryParams from(final String queryString) {
        final String[] rawQueryParams = queryString.split(QUERY_PARAM_SEPARATOR);
        final Map<String, String> queryParams = new HashMap<>();
        for (String queryParam : rawQueryParams) {
            final String[] queryParamKeyValue = queryParam.split(QUERY_PARAM_KEY_VALUE_SEPARATOR);
            queryParams.put(
                    queryParamKeyValue[RAW_QUERY_PARAM_KEY_INDEX],
                    queryParamKeyValue[RAW_QUERY_PARAM_VALUE_INDEX]
            );
        }

        return new QueryParams(queryParams);
    }

    public Optional<String> getValue(final String key) {
        return Optional.ofNullable(queryParams.get(key));
    }
}
