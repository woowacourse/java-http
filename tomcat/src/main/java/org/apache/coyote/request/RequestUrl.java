package org.apache.coyote.request;

import java.net.URL;
import java.util.Map;

public class RequestUrl {

    private static final String HOME_PATH = "/";
    private static final String DEFAULT_RESOURCE_PATH = "/static";
    private static final String INDEX_HTML_FILE_NAME = "/index.html";
    private static final String NOT_FOUND_RESOURCE_PATH = "/static/404.html";

    private final URL path;
    private final Map<String, String> queryString;

    public static RequestUrl of(String path, Map<String, String> queryString) {
        if (RequestUrl.class.getResource(DEFAULT_RESOURCE_PATH + path) == null) {
            return new RequestUrl(RequestUrl.class.getResource(NOT_FOUND_RESOURCE_PATH), queryString);
        }
        if (path.equals(HOME_PATH)) {
            return new RequestUrl(RequestUrl.class.getResource(DEFAULT_RESOURCE_PATH + INDEX_HTML_FILE_NAME),
                    queryString);
        }
        return new RequestUrl(RequestUrl.class.getResource(DEFAULT_RESOURCE_PATH + path), queryString);
    }

    private RequestUrl(URL path, Map<String, String> queryString) {
        this.path = path;
        this.queryString = queryString;
    }

    public boolean isNullPath() {
        return path.equals(RequestUrl.class.getResource(NOT_FOUND_RESOURCE_PATH));
    }

    public URL getPath() {
        return path;
    }

    public Map<String, String> getQueryString() {
        return queryString;
    }
}
