package org.apache.coyote.http11.utils;

import java.util.HashMap;

public class QueryParamsParser {

    private static final int REQUEST_URL_QUERY_PARAM_START_INDEX = 1;
    private static final int PARAM_INFO_INDEX = 0;
    private static final int PARAM_VALUE_INDEX = 1;

    public static HashMap<String, String> parseLoginParams(final String requestUrl) {
        final HashMap<String, String> loginData = new HashMap<>();
        if (!requestUrl.contains("?") || !requestUrl.contains("=")) {
            return loginData;
        }
        final String queryParams = requestUrl.split("\\?")[REQUEST_URL_QUERY_PARAM_START_INDEX];
        final String[] params = queryParams.split("&");

        for (String param : params) {
            final String paramInfo = param.split("=")[PARAM_INFO_INDEX];
            final String paramValue = param.split("=")[PARAM_VALUE_INDEX];
            loginData.put(paramInfo, paramValue);
        }
        return loginData;
    }
}
