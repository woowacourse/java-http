package org.apache.coyote.http11;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class QueryParams {

    private String path;
    private Map<String, String> params;

    private QueryParams(String path, Map<String, String> params) {
        this.path = path;
        this.params = params;
    }

    public static QueryParams from(String requestURL) {
        if (requestURL.contains("?")) {
            int index = requestURL.indexOf("?");
            String path = requestURL.substring(0, index);
            String queryString = requestURL.substring(index + 1);
            Map<String, String>params = parsingQueryString(queryString);
            return new QueryParams(path, params);
        }
        return new QueryParams(requestURL, Collections.emptyMap());
    }

    private static Map<String, String> parsingQueryString(String queryString) {
        Map<String, String> params = new HashMap<>();
        String[] paramsLine = queryString.split("&");
        for (int i = 0; i < paramsLine.length; i++) {
            String[] paramsKeyAndValue = paramsLine[i].split("=");
            params.put(paramsKeyAndValue[0], paramsKeyAndValue[1]);
        }
        return params;
    }

    public boolean isLoginPath() {
        return path.equals("/login") && !params.isEmpty();
    }

    public boolean isEmpty() {
        return params.isEmpty();
    }

    public String getParamValue(String paramKey) {
        return params.get(paramKey);
    }
}
