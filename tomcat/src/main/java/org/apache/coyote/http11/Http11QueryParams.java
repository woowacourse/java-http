package org.apache.coyote.http11;

import static java.util.stream.Collectors.toMap;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.exception.InvalidQueryParamKeyException;

public class Http11QueryParams {
    private static final String PARAM_DELIMITER = "&";
    private static final String KEY_VALUE_DELIMITER = "=";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> params;

    public Http11QueryParams(final Map<String,String> params) {
        this.params = params;
    }

    public static Http11QueryParams of(final String urlQueryParams) {
        final Map<String, String> params = Arrays.stream(urlQueryParams.split(PARAM_DELIMITER))
                .map(keyValue -> keyValue.split(KEY_VALUE_DELIMITER))
                .collect(toMap(keyValue -> keyValue[KEY_INDEX], keyValue -> keyValue[VALUE_INDEX]));
        return new Http11QueryParams(params);
    }

    public static Http11QueryParams ofEmpty() {
        return new Http11QueryParams(new HashMap<>());
    }

    public String get(final String key) {
        final String value = params.get(key);
        if (value == null) {
            throw new InvalidQueryParamKeyException();
        }
        return value;
    }
}
