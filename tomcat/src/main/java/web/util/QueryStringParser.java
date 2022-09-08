package web.util;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class QueryStringParser {

    private static final String QUERY_STRING_ELEMENT_DELIMITER = "&";
    private static final String QUERY_STRING_KEY_VALUE_DELIMITER = "=";

    public static Map<String, String> parseQueryString(final String value) {
        Map<String, String> queryString = Arrays.stream(
                value.split(QUERY_STRING_ELEMENT_DELIMITER)
        ).collect(Collectors.toMap(
                i -> i.split(QUERY_STRING_KEY_VALUE_DELIMITER)[0],
                i -> i.split(QUERY_STRING_KEY_VALUE_DELIMITER)[1]
        ));
        return queryString;
    }
}
