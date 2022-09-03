package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class RequestUri {

    private static final String STATIC_FILE_PATH = "static";

    private final String uri;
    private final String resourcePath;
    private final Map<String, String> queryParams;

    private RequestUri(final String uri, final String resourcePath, final Map<String, String> queryParams) {
        this.uri = uri;
        this.resourcePath = resourcePath;
        this.queryParams = queryParams;
    }

    public static RequestUri of(final String uri) {
        return new RequestUri(uri, parseResourcePath(uri), parseQueryParams(uri));
    }

    private static String parseResourcePath(String uri) {
        int index = uri.indexOf("?");
        if (index != -1) {
            return STATIC_FILE_PATH + uri.substring(0, index) + ".html";
        }
        return STATIC_FILE_PATH + uri;
    }

    private static Map<String, String> parseQueryParams(final String uri) {
        Map<String, String> queryParams = new HashMap<>();
        int index = uri.indexOf("?");
        if (index != -1) {
            String[] queryString = uri.substring(index + 1).split("&");
            for (String element : queryString) {
                String[] split = element.split("=");
                queryParams.put(split[0], split[1]);
            }
        }
        return queryParams;
    }

    public String findQueryParamValue(String key) {
        return queryParams.get(key);
    }

    public Boolean hasQueryParams() {
        return !queryParams.isEmpty();
    }

    public Boolean isResourceFileRequest() {
        return !uri.equals("/");
    }

    public String getUri() {
        return uri;
    }

    public String getResourcePath() {
        return resourcePath;
    }
}
