package org.apache.coyote.http11.model;

import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    public static final String REQUEST_LINE_DELIMITER = " ";
    public static final int PATH_INDEX = 1;
    public static final String EXIST_QUERY_PARAMS = "?";
    public static final String QUERY_PARAMS_DELIMITER = "&";
    public static final String QUERY_PRAM_VALUE_DELIMITER = "=";
    public static final int QUERY_PRAM_KEY_INDEX = 0;
    public static final int QUERY_PARAM_VALUE_INDEX = 1;

    private final String path;
    private final Map<String, String> queryParams;

    private HttpRequest(final String path, final Map<String, String> queryParams) {
        this.path = path;
        this.queryParams = queryParams;
    }

    public static HttpRequest of(final String uri) {
        String[] readLine = uri.split(REQUEST_LINE_DELIMITER);
        String path = getPath(readLine[PATH_INDEX]);
        Map<String, String> queryParams = getQueryParams(readLine[PATH_INDEX]);
        return new HttpRequest(path, queryParams);
    }

    private static String getPath(String input) {
        if (!input.contains(EXIST_QUERY_PARAMS)) {
            return input;
        }
        return input.substring(0, input.lastIndexOf(EXIST_QUERY_PARAMS));
    }

    private static Map<String, String> getQueryParams(final String path) {
        Map<String, String> queryParams = new HashMap<>();
        if (!path.contains(EXIST_QUERY_PARAMS)) {
            return queryParams;
        }

        String queryString = path.substring(path.lastIndexOf(EXIST_QUERY_PARAMS) + 1);
        String[] queryParam = queryString.split(QUERY_PARAMS_DELIMITER);
        for (String param : queryParam) {
            String[] split1 = param.split(QUERY_PRAM_VALUE_DELIMITER);
            queryParams.put(split1[QUERY_PRAM_KEY_INDEX], split1[QUERY_PARAM_VALUE_INDEX]);
        }
        return queryParams;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }
}
