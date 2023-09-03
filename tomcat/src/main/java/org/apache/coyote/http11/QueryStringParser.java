package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class QueryStringParser {

    public static final String PARAMS_DELIMITER = "&";
    public static final String PARAM_VALUE_DELIMITER = "=";

    public static final int PARAM_INDEX = 0;
    public static final int VALUE_INDEX = 1;

    private final Map<String, String> paramAndValues;

    private QueryStringParser(Map<String, String> paramAndValues) {
        this.paramAndValues = paramAndValues;
    }

    public static QueryStringParser from(String queryString) {
        String[] queryParams = queryString.split(PARAMS_DELIMITER);

        Map<String, String> paramAndValues = new HashMap<>();
        for (String queryParam : queryParams) {
            String[] paramAndValue = queryParam.split(PARAM_VALUE_DELIMITER);
            String param = paramAndValue[PARAM_INDEX];
            String value = paramAndValue[VALUE_INDEX];
            paramAndValues.put(param, value);
        }

        return new QueryStringParser(paramAndValues);
    }

    public Map<String, String> getParamAndValues() {
        return paramAndValues;
    }
}
