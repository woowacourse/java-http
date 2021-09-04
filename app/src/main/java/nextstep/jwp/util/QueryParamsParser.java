package nextstep.jwp.util;

import java.util.Arrays;
import java.util.Map;

public class QueryParamsParser {
    private static final String QUERY_STRING_DELIMITER = "&";
    private static final String QUERY_KEY_VALUE_DELIMITER = "=";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private QueryParamsParser() {
    }

    public static void parse(final String queryString, final Map<String, String> queryParameters) {
        Arrays.stream(queryString.split(QUERY_STRING_DELIMITER))
                .map(query -> query.split(QUERY_KEY_VALUE_DELIMITER))
                .forEach(queryPair -> queryParameters.put(queryPair[KEY_INDEX], queryPair[VALUE_INDEX]));
    }
}
