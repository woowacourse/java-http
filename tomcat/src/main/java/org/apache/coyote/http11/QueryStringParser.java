package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class QueryStringParser {

    private static final String URL_DELIMITER = "?";
    private static final String REQUEST_PARAM_DELIMITER = "&";
    private static final String VALUE_DELIMITER = "=";


    public static Map<Integer, String> parsing(final String requestUrl) {
        final int index = requestUrl.indexOf(URL_DELIMITER);
        final String queryString = requestUrl.substring(index + 1);

        final String[] requestParams = queryString.split(REQUEST_PARAM_DELIMITER);
        final Map<Integer, String> map = new HashMap<>();

        int order = 0;
        for (String requestParam : requestParams) {
            final String[] values = requestParam.split(VALUE_DELIMITER);
            map.put(order++, values[1]);
        }
        return map;
    }
}
