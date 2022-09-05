package org.apache.coyote.http11.util;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.HttpMethod;

public class HttpStartLineParser {

    private static final String START_LINE_DELIMITER = " ";
    private static final String QUERY_STRING_DELIMITER = "?";
    private static final String QUERY_PARAM_DELIMITER = "&";
    private static final String KEY_AND_VALUE_DELIMITER = "=";

    private final HttpMethod httpMethod;
    private final String httpUrl;
    private final Map<String, String> queryParams = new HashMap<>();

    public HttpStartLineParser(String startLine) {
        String[] startLines = startLine.split(START_LINE_DELIMITER);
        httpMethod = HttpMethod.from(startLines[0]);
        httpUrl = parseUrlWithQueryString(startLines[1]);
    }

    private String parseUrlWithQueryString(String url) {
        if (hasQueryString(url)) {
            parseQueryParams(url);
            return rejectQueryStringFromUrl(url);
        }
        return url;
    }

    private boolean hasQueryString(String url) {
        return url.contains(QUERY_STRING_DELIMITER);
    }

    private void parseQueryParams(String url) {
        String queryString = url.split("\\?")[1];
        String[] keyAndValues = queryString.split(QUERY_PARAM_DELIMITER);
        parseKeyAndValues(keyAndValues);
    }

    private void parseKeyAndValues(String[] keyAndValues) {
        for (String keyAndValue : keyAndValues) {
            String[] keyValue = keyAndValue.split(KEY_AND_VALUE_DELIMITER);
            String key = keyValue[0];
            String value = keyValue[1];
            queryParams.put(key, value);
        }
    }

    private String rejectQueryStringFromUrl(String url) {
        int queryStringDelimiterIndex = url.indexOf(QUERY_STRING_DELIMITER);
        return url.substring(0, queryStringDelimiterIndex);
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getHttpUrl() {
        return httpUrl;
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }
}
