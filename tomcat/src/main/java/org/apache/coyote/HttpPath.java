package org.apache.coyote;

import java.util.HashMap;
import java.util.Map;

public class HttpPath {

    private static final String REQUEST_PARAM_DELIMITER = "?";
    private static final String PARAMS_DELIMITER = "&";
    private static final String KEY_VALUE_DELIMITER = "=";
    private static final String EXTENSION_DOT = ".";

    private static final int KEY = 0;
    private static final int VALUE = 1;
    private static final String EMPTY_VALUE = "";
    private static final int EMPTY_VALUE_PARAM_LENGTH = 1;

    private static final int START_INDEX = 0;
    private static final int ONE_INDEX = 1;


    private final String value;
    private final Map<String, String> params;

    private HttpPath(final String value, final Map<String, String> params) {
        this.value = value;
        this.params = new HashMap<>(params);
    }

    public static HttpPath from(final String uri) {
        final Map<String, String> params = new HashMap<>();
        String path = uri;

        if (uri.contains(REQUEST_PARAM_DELIMITER)) {
            final int paramStartIndex = uri.indexOf(REQUEST_PARAM_DELIMITER);
            path = uri.substring(START_INDEX, paramStartIndex);
            final String queryString = uri.substring(paramStartIndex + ONE_INDEX);

            parseQueryParams(params, queryString);
        }

        return new HttpPath(path, params);
    }

    private static void parseQueryParams(final Map<String, String> params, final String queryString) {
        final String[] queryParams = queryString.split(PARAMS_DELIMITER);

        for (final String queryParam : queryParams) {
            parseQueryParam(params, queryParam);
        }
    }

    private static void parseQueryParam(final Map<String, String> params, final String queryParam) {
        final String[] queryParamKeyValue = queryParam.split(KEY_VALUE_DELIMITER);

        if (haveEmptyValue(queryParamKeyValue)) {
            params.put(queryParamKeyValue[KEY], EMPTY_VALUE);
            return;
        }
        params.put(queryParamKeyValue[KEY], queryParamKeyValue[VALUE]);
    }

    private static boolean haveEmptyValue(final String[] queryParamKeyValue) {
        return queryParamKeyValue.length == EMPTY_VALUE_PARAM_LENGTH;
    }

    public String getValue() {
        return value;
    }

    public String getParam(final String key) {
        return params.get(key);
    }

    public boolean isResource() {
        return value.contains(EXTENSION_DOT);
    }
}
