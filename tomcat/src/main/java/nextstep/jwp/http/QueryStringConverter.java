package nextstep.jwp.http;

import java.util.HashMap;
import java.util.Map;

public class QueryStringConverter {

    private static final String APPEND_DELIMITER = "&";
    private static final String EQUAL_DELIMITER = "=";
    private static final int QUERY_PARAM_KEY_VALUE_SIZE = 2;
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private QueryStringConverter() {
    }

    public static Map<String, String> convert(final String queryString) {
        final Map<String, String> paramMapping = new HashMap<>();
        if (queryString == null) {
            return paramMapping;
        }
        for (String info : queryString.split(APPEND_DELIMITER)) {
            putKeyValue(paramMapping, info);
        }
        return paramMapping;
    }

    private static void putKeyValue(final Map<String, String> paramMapping, final String info) {
        final String[] queryParam = info.split(EQUAL_DELIMITER);
        if (queryParam.length == QUERY_PARAM_KEY_VALUE_SIZE) {
            paramMapping.put(queryParam[KEY_INDEX], queryParam[VALUE_INDEX]);
        }
    }
}
