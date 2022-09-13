package org.apache.coyote.http11;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RequestURL {

    private static final String PARAM_REGEX = "&";
    private static final String KEY_AND_VALUE_REGEX = "=";
    private static final String MAIN_REQUEST_URL = "/";
    private static final String LOGIN_REQUEST_URL = "/login";
    private static final String HTML_EXTENSION = ".html";

    private String path;
    private final Map<String, String> queryParams;

    private RequestURL(String path, Map<String, String> queryParams) {
        this.path = path;
        this.queryParams = queryParams;
    }

    public static RequestURL from(String requestURL) {
        if (requestURL.contains("?")) {
            int index = requestURL.indexOf("?");
            String path = requestURL.substring(0, index);
            String queryString = requestURL.substring(index + 1);
            Map<String, String> params = parsingQueryString(queryString);
            return new RequestURL(path, params);
        }
        return new RequestURL(requestURL, Collections.emptyMap());
    }

    private static Map<String, String> parsingQueryString(String queryString) {
        Map<String, String> params = new HashMap<>();
        String[] paramsLine = queryString.split(PARAM_REGEX);
        for (int i = 0; i < paramsLine.length; i++) {
            String[] paramsKeyAndValue = paramsLine[i].split(KEY_AND_VALUE_REGEX);
            params.put(paramsKeyAndValue[0], paramsKeyAndValue[1]);
        }
        return params;
    }

    public void changeRequestURL() {
        if (LOGIN_REQUEST_URL.equals(path)) {
            path += HTML_EXTENSION;
        }
    }

    public boolean isMainRequest() {
        return MAIN_REQUEST_URL.equals(path);
    }

    public boolean isLoginRequest() {
        return path.equals(LOGIN_REQUEST_URL + HTML_EXTENSION) && !queryParams.isEmpty();
    }

    public String getParamValue(String paramKey) {
        return queryParams.get(paramKey);
    }

    public String getPath() {
        return path;
    }
}
