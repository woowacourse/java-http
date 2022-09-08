package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;

public class HttpPath {

    private static final String QUERY_PARAM_DELIMITER = "?";
    private static final String QUERY_PARAM_AND_DELIMITER = "&";
    private static final String QUERY_PARAM_VALUE_DELIMITER = "=";
    private static final int PARAM_NAME_INDEX = 0;
    private static final int PARAM_VALUE_INDEX = 1;

    private final String path;
    private final Map<String, String> params;

    public HttpPath(final String path, final Map<String, String> params) {
        this.path = path;
        this.params = params;
    }

    public static HttpPath from(final String uri) {
        final String path = parsePath(uri);
        final Map<String, String> params = parseParams(uri);
        return new HttpPath(path, params);
    }

    private static String parsePath(final String uri) {
        if (uri.contains(QUERY_PARAM_DELIMITER)) {
            return uri.substring(0, uri.indexOf(QUERY_PARAM_DELIMITER));
        }
        return uri;
    }

    private static Map<String, String> parseParams(final String uri) {
        final Map<String, String> params = new HashMap<>();
        if (uri.contains(QUERY_PARAM_DELIMITER)) {
            final String[] allParams = uri.split(QUERY_PARAM_AND_DELIMITER);
            addParams(params, allParams);
        }
        return params;
    }

    private static void addParams(final Map<String, String> params, final String[] allParams) {
        final int paramCount = allParams.length;
        int index = 0;
        while (index++ < paramCount) {
            final String[] param = allParams[index].split(QUERY_PARAM_VALUE_DELIMITER);
            params.put(param[PARAM_NAME_INDEX], param[PARAM_VALUE_INDEX]);
        }
    }
}
