package org.apache.coyote.http11;

import java.util.Map;
import org.apache.coyote.http11.utils.PairConverter;

public class QueryParam {

    private static final int QUERY_PARAM = 1;
    private static final String KEY_VALUE_PAIR_DELIMITER = "&";
    private static final String KEY_VALUE_DELIMITER = "=";
    private static final String QUERY_PARAM_DELIMITER = "?";

    private final Map<String, String> parameters;

    public QueryParam(final String path) {
        this.parameters = PairConverter.toMap(toQueryString(path), KEY_VALUE_PAIR_DELIMITER, KEY_VALUE_DELIMITER);
    }

    private String toQueryString(final String path) {
        return path.split("\\"+ QUERY_PARAM_DELIMITER)[QUERY_PARAM];
    }

    public static boolean isQueryParam(String path) {
        return path.contains(QUERY_PARAM_DELIMITER);
    }

    public boolean matchParameters(String key) {
        return parameters.containsKey(key);
    }

    public String getValue(String key) {
        return parameters.get(key);
    }
}
