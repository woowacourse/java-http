package org.apache.coyote.http11.request;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RequestURL {

    private static final String PARAM_REGEX = "&";
    private static final String KEY_AND_VALUE_REGEX = "=";

    private final String path;
    private final Map<String, String> queryParams;

    private RequestURL(final String path, final Map<String, String> queryParams) {
        this.path = path;
        this.queryParams = queryParams;
    }

    public static RequestURL from(final String requestURL) {
        if (requestURL.contains("?")) {
            final int index = requestURL.indexOf("?");
            final String path = requestURL.substring(0, index);
            final String queryString = requestURL.substring(index + 1);
            final Map<String, String> params = parsingQueryString(queryString);
            return new RequestURL(path, params);
        }
        return new RequestURL(requestURL, Collections.emptyMap());
    }

    public static Map<String, String> parsingQueryString(final String queryString) {
        final Map<String, String> params = new HashMap<>();
        final String[] paramsLine = queryString.split(PARAM_REGEX);
        for (int i = 0; i < paramsLine.length; i++) {
            final String[] paramsKeyAndValue = paramsLine[i].split(KEY_AND_VALUE_REGEX);
            params.put(paramsKeyAndValue[0], paramsKeyAndValue[1]);
        }
        return params;
    }

    public String getPath() {
        return path;
    }
}
