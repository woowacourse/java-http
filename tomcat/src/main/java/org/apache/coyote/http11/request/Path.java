package org.apache.coyote.http11.request;

import java.util.Map;
import nextstep.jwp.utils.QueryParser;

public class Path {

    private static final String QUERY_STRING_START_DELIMITER = "\\?";
    private static final String QUERY_STRING_START_TEXT = "?";
    private static final int URL_INDEX = 0;
    private static final int PARAMS_INDEX = 1;

    private final String url;
    private final Map<String, String> requestParams;

    public Path(String url, Map<String, String> requestParams) {
        this.url = url;
        this.requestParams = requestParams;
    }

    public static Path parsePath(String path) {
        String url = path.split(QUERY_STRING_START_DELIMITER)[URL_INDEX];
        Map<String, String> requestParams = parseRequestParams(path);
        return new Path(url, requestParams);
    }

    private static Map<String, String> parseRequestParams(final String path) {
        if (!path.contains(QUERY_STRING_START_TEXT)) {
            return Map.of();
        }

        String params = path.split(QUERY_STRING_START_DELIMITER)[PARAMS_INDEX];
        return QueryParser.parse(params);
    }

    public String getUrl() {
        return url;
    }

    public Map<String, String> getRequestParams() {
        return requestParams;
    }
}
