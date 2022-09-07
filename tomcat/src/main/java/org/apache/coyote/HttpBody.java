package org.apache.coyote;

import java.util.HashMap;
import java.util.Map;

public class HttpBody {

    private static final String PARAMS_DELIMITER = "&";
    private static final String KEY_VALUE_DELIMITER = "=";

    private static final int KEY = 0;
    private static final int VALUE = 1;
    private static final String EMPTY_VALUE = "";
    private static final int EMPTY_VALUE_PARAM_LENGTH = 1;

    private final Map<String, String> params;

    private HttpBody(final Map<String, String> params) {
        this.params = new HashMap<>(params);
    }

    public static HttpBody from(final String body) {
        final HashMap<String, String> params = new HashMap<>();
        if (body == null || body.isBlank()) {
            return new HttpBody(params);
        }

        final String[] queryParams = body.split(PARAMS_DELIMITER);

        for (final String queryParam : queryParams) {
            parseQueryParam(params, queryParam);
        }
        return new HttpBody(params);
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

    @Override
    public String toString() {
        return "HttpBody{" +
                "params=" + params +
                '}';
    }
}
