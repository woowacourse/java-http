package org.apache.coyote.request;

import java.util.HashMap;
import java.util.Map;

public class QueuryParam {

    public static final String QUERY_INDICATOR = "?";
    public static final String QUERY_COMPONENT_DELIMITER = "&";
    public static final String QUERY_COMPONENT_VALUE_DELIMITER = "=";

    private final Map<String, String> params;

    public QueuryParam(String requestLineEntry) {
        this.params = mapQueryParam(requestLineEntry);
    }

    private Map<String, String> mapQueryParam(String requestLineEntry) {
        Map<String, String> mappedQueryParams = new HashMap<>();
        if (!requestLineEntry.contains(QUERY_INDICATOR)) {
            return mappedQueryParams;
        }

        int queryParamIndex = requestLineEntry.indexOf(QUERY_INDICATOR);
        String queryString = requestLineEntry.substring(queryParamIndex + 1);
        String[] splittedQueryString = queryString.split(QUERY_COMPONENT_DELIMITER);

        for (String queryParamEntry : splittedQueryString) {
            mappedQueryParams.put(
                    queryParamEntry.split(QUERY_COMPONENT_VALUE_DELIMITER)[0],
                    queryParamEntry.split(QUERY_COMPONENT_VALUE_DELIMITER)[1]
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
