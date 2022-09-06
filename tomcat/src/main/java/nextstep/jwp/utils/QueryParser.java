package nextstep.jwp.utils;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class QueryParser {

    private static final String QUERY_STRING_DELIMITER = "&";
    private static final String KEY_VALUE_SEPARATOR = "=";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    public static Map<String, String> parse(String query) {
        return Arrays.stream(query.split(QUERY_STRING_DELIMITER))
                .map(it -> it.split(KEY_VALUE_SEPARATOR))
                .collect(Collectors.toMap(it -> it[KEY_INDEX], it -> it[VALUE_INDEX]));
    }
}
