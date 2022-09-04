package org.apache.coyote.http11.utils;

import java.util.HashMap;

public class QueryParamsParser {

    private static final int REQUEST_URL_QUERY_PARAM_START_INDEX = 1;
    private static final int PARAM_INFO_INDEX = 0;
    private static final int PARAM_VALUE_INDEX = 1;

    public static HashMap<String, String> parse(final String requestUrl) {
        final HashMap<String, String> data = new HashMap<>();
        if (!requestUrl.contains("?") || !requestUrl.contains("=")) {
            return data;
        }
        final String queryParams = requestUrl.split("\\?")[REQUEST_URL_QUERY_PARAM_START_INDEX];
        final String[] params = queryParams.split("&");
        initData(data, params);
        return data;
    }

    private static void initData(final HashMap<String, String> data, final String[] params) {
        for (final String param : params) {
            final String paramInfo = param.split("=")[PARAM_INFO_INDEX];
            final String paramValue = param.split("=")[PARAM_VALUE_INDEX];
            data.put(paramInfo, paramValue);
        }
    }
}
