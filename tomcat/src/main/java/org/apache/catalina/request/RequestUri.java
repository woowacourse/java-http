package org.apache.catalina.request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.catalina.parser.HttpRequestParser;

public class RequestUri {
    private static final String QUERY_PARAMETER_DELIMITER = "\\?";
    private static final String QUERY_PARAMETER_SEPARATOR = "&";
    public static final int PATH_QUERY_PARAMETER_INDEX = 1;
    public static final int PATH_WITHOUT_QUERY_INDEX = 0;
    private static final int PATH_LIMIT = 2;
    private final String path;
    private final Map<String, String> queryParam;

    public RequestUri(String url) {
        this.path = url;
        this.queryParam = findQueryParams(path);
    }

    private Map<String, String> findQueryParams(String path) {
        String[] separationUrl = path.split(QUERY_PARAMETER_DELIMITER, PATH_LIMIT);
        if (separationUrl.length >= PATH_LIMIT) {
            List<String> params = List.of(separationUrl[PATH_QUERY_PARAMETER_INDEX].split(QUERY_PARAMETER_SEPARATOR));
            return HttpRequestParser.parseParamValues(params);
        }
        return new HashMap<>();
    }

    public boolean checkQueryParamIsEmpty() {
        return queryParam.isEmpty();
    }

    public String getPathWithoutQuery() {
        return path.split(QUERY_PARAMETER_DELIMITER, PATH_LIMIT)[PATH_WITHOUT_QUERY_INDEX];
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getQueryParam() {
        return new HashMap<>(queryParam);
    }
}
