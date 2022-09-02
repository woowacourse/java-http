package org.apache.coyote.http11;

import java.util.Map;

public class ArgumentResolver {

    private static final int NEXT_INDEX = 1;
    private static final String KEY_VALUE_DELIMITER = "=";
    private static final String QUERY_PARAM_DELIMITER = "&";
    private static final int FIRST_INDEX = 0;

    public static Map<String, String> resolve(final String queryString) {
        final String[] split = queryString.split(QUERY_PARAM_DELIMITER);

        final String[] split1 = split[FIRST_INDEX].split(KEY_VALUE_DELIMITER);
        final String[] split2 = split[NEXT_INDEX].split(KEY_VALUE_DELIMITER);

        return Map.of(split1[FIRST_INDEX], split1[NEXT_INDEX], split2[FIRST_INDEX], split2[NEXT_INDEX]);
    }
}
