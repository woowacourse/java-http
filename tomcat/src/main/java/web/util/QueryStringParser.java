package web.util;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class QueryStringParser {

    private static final String QUERY_STRING_ELEMENT_DELIMITER = "&";
    private static final String QUERY_STRING_KEY_VALUE_DELIMITER = "=";
    private static final int ELEMENT_KEY_INDEX = 0;
    private static final int ELEMENT_VALUE_INDEX = 1;

    public static Map<String, String> parseQueryString(final String value) {
        return Arrays.stream(
                value.split(QUERY_STRING_ELEMENT_DELIMITER)
        ).collect(Collectors.toMap(
                i -> i.split(QUERY_STRING_KEY_VALUE_DELIMITER)[ELEMENT_KEY_INDEX],
                i -> i.split(QUERY_STRING_KEY_VALUE_DELIMITER)[ELEMENT_VALUE_INDEX]
        ));
    }
}
