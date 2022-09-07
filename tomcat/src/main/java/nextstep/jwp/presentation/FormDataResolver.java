package nextstep.jwp.presentation;

import java.util.HashMap;
import java.util.Map;

public class FormDataResolver {
    private static final String KEY_VALUE_DELIMITER = "=";
    private static final String QUERY_PARAM_DELIMITER = "&";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    public static Map<String, String> resolve(final String requestBody) {
        final String[] queryParams = requestBody.split(QUERY_PARAM_DELIMITER);

        final Map<String, String> parsedQueryParams = new HashMap<>();
        for (final String queryParam : queryParams) {
            final String[] keyAndValue = queryParam.split(KEY_VALUE_DELIMITER);
            parsedQueryParams.put(keyAndValue[KEY_INDEX], keyAndValue[VALUE_INDEX]);
        }
        return parsedQueryParams;
    }
}
