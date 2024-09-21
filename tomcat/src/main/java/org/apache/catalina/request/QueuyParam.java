package org.apache.catalina.request;

import java.util.HashMap;
import java.util.Map;

public class QueuyParam {

    public static final String QUERY_INDICATOR = "?";
    public static final String COMPONENT_DELIMITER = "&";
    public static final String COMPONENT_VALUE_DELIMITER = "=";
    public static final int KEY_INDEX = 0;
    public static final int VALUE_INDEX = 1;

    private final Map<String, String> params;

    public QueuyParam(String requestLineEntry) {
        this.params = mapQueryParam(requestLineEntry);
    }

    private Map<String, String> mapQueryParam(String requestLineEntry) {
        Map<String, String> mappedQueryParams = new HashMap<>();
        if (!requestLineEntry.contains(QUERY_INDICATOR)) {
            return mappedQueryParams;
        }

        int queryParamIndex = requestLineEntry.indexOf(QUERY_INDICATOR);
        String queryString = requestLineEntry.substring(queryParamIndex + 1);
        String[] splittedQueryString = queryString.split(COMPONENT_DELIMITER);

        for (String queryParamEntry : splittedQueryString) {
            mappedQueryParams.put(
                    queryParamEntry.split(COMPONENT_VALUE_DELIMITER)[KEY_INDEX],
                    queryParamEntry.split(COMPONENT_VALUE_DELIMITER)[VALUE_INDEX]
            );
        }
        return mappedQueryParams;
    }

    public boolean hasQueryParam() {
        return !params.isEmpty();
    }

    public String getQueryParam(String paramName) {
        return params.get(paramName);
    }
}
