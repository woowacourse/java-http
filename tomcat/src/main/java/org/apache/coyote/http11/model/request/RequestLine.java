package org.apache.coyote.http11.model.request;

import java.util.HashMap;
import java.util.Map;

public class RequestLine {

    private static final String SEPARATOR = " ";
    private static final int METHOD_INDEX = 0;
    private static final int URL_INDEX = 1;
    public static final String QUERY_STRING_SEPARATOR = "\\?";
    public static final String QUERY_PARAM_SEPARATOR = "&";
    public static final String PARAM_KEY_VALUE_SEPARATOR = "=";
    public static final String QUERY_PARAM_INDICATOR = "?";

    private final String method;
    private final String url;
    private final Map<String, String> queryParams = new HashMap<>();

    public RequestLine(final String line) {
        String[] split = line.split(SEPARATOR);

        this.method = split[METHOD_INDEX];
        this.url = extractUrl(split[URL_INDEX]);
        extractQueryParams(split[URL_INDEX]);

    }

    private String extractUrl(final String url) {
        String[] splitUrl = url.split(QUERY_STRING_SEPARATOR);
        return splitUrl[0];
    }

    private void extractQueryParams(final String url) {
        if (hasQueryParams(url)) {
            String[] splitUrl = url.split(QUERY_STRING_SEPARATOR);
            String rawQueryParams = splitUrl[1];
            putAll(rawQueryParams.split(QUERY_PARAM_SEPARATOR));
        }
    }

    private void putAll(final String[] splitQueryParams) {
        for (String queryParam : splitQueryParams) {
            String[] splitQueryParam = queryParam.split(PARAM_KEY_VALUE_SEPARATOR);
            queryParams.put(splitQueryParam[0], splitQueryParam[1]);
        }
    }

    private boolean hasQueryParams(final String url) {
        return url.contains(QUERY_PARAM_INDICATOR);
    }

    public String getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }

    public String getQueryParam(final String key) {
        return queryParams.get(key);
    }
}
