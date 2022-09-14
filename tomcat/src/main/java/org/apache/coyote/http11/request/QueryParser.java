package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;

public class QueryParser {

    private static final String PARAM_REGEX = "&";
    private static final String KEY_AND_VALUE_REGEX = "=";

    private final Map<String, String> value;

    public QueryParser(final Map<String, String> value) {
        this.value = value;
    }

    public static Map<String, String> parsingQueryString(final String queryString) {
        final Map<String, String> params = new HashMap<>();
        final String[] paramsLine = queryString.split(PARAM_REGEX);
        for (int i = 0; i < paramsLine.length; i++) {
            final String[] paramsKeyAndValue = paramsLine[i].split(KEY_AND_VALUE_REGEX);
            params.put(paramsKeyAndValue[0], paramsKeyAndValue[1]);
        }
        return params;
    }
}
