package org.apache.coyote.http11.model;

import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    private static final String REQUEST_LINE_DELIMITER = " ";
    private static final int PATH_INDEX = 1;
    private static final String EXIST_QUERY_PARAMS = "?";
    private static final String QUERY_PARAMS_DELIMITER = "&";
    private static final String QUERY_PRAM_VALUE_DELIMITER = "=";
    private static final int QUERY_PRAM_KEY_INDEX = 0;
    private static final int QUERY_PARAM_VALUE_INDEX = 1;

    private final String path;
    private final Map<String, String> queryParams;

    private HttpRequest(final String path, final Map<String, String> queryParams) {
        this.path = path;
        this.queryParams = queryParams;
    }

    public static HttpRequest from(final String uri) {
        String[] readLine = uri.split(REQUEST_LINE_DELIMITER);
        String path = getPath(readLine[PATH_INDEX]);
        Map<String, String> queryParams = getQueryParams(readLine[PATH_INDEX]);
        return new HttpRequest(path, queryParams);
    }

    private static String getPath(final String input) {
        if (existQueryParams(input)) {
            return input;
        }
        return input.substring(0, input.lastIndexOf(EXIST_QUERY_PARAMS));
    }

    private static Map<String, String> getQueryParams(final String path) {
        Map<String, String> queryParams = new HashMap<>();
        if (existQueryParams(path)) {
            return queryParams;
        }

        String[] queryString = getQueryString(path);
        for (String param : queryString) {
            String[] paramsInfo = param.split(QUERY_PRAM_VALUE_DELIMITER);
            queryParams.put(paramsInfo[QUERY_PRAM_KEY_INDEX], paramsInfo[QUERY_PARAM_VALUE_INDEX]);
        }
        return queryParams;
    }

    private static String[] getQueryString(final String path) {
        String queryString = path.substring(path.lastIndexOf(EXIST_QUERY_PARAMS) + 1);
        return queryString.split(QUERY_PARAMS_DELIMITER);
    }

    private static boolean existQueryParams(final String path) {
        return !path.contains(EXIST_QUERY_PARAMS);
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }
}
