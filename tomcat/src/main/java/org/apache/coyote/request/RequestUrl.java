package org.apache.coyote.request;

import java.net.URL;
import java.util.Map;
import java.util.Objects;

public class RequestUrl {

    private static final String HOME_PATH = "/";
    private static final String DEFAULT_RESOURCE_PATH = "/static";
    private static final String INDEX_HTML_FILE_NAME = "/index.html";
    private static final String NOT_FOUND_RESOURCE_PATH = "/static/404.html";
    private static final String HTML_TYPE = ".html";

    private final String path;
    private final URL url;
    private final Map<String, String> queryString;

    public static RequestUrl of(String path, Map<String, String> queryString) {
        if (RequestUrl.class.getResource(DEFAULT_RESOURCE_PATH + path) == null &&
                RequestUrl.class.getResource(DEFAULT_RESOURCE_PATH + path + HTML_TYPE) != null) {
            return new RequestUrl(path, RequestUrl.class.getResource(DEFAULT_RESOURCE_PATH + path + HTML_TYPE), queryString);
        }
        if (RequestUrl.class.getResource(DEFAULT_RESOURCE_PATH + path) == null) {
            return new RequestUrl(path, RequestUrl.class.getResource(NOT_FOUND_RESOURCE_PATH), queryString);
        }
        if (path.equals(HOME_PATH)) {
            return new RequestUrl(path, RequestUrl.class.getResource(DEFAULT_RESOURCE_PATH + INDEX_HTML_FILE_NAME),
                    queryString);
        }
        return new RequestUrl(path, RequestUrl.class.getResource(DEFAULT_RESOURCE_PATH + path), queryString);
    }

    private RequestUrl(String path, URL url, Map<String, String> queryString) {
        this.path = path;
        this.url = url;
        this.queryString = queryString;
    }

    public boolean isNullPath() {
        return url.equals(RequestUrl.class.getResource(NOT_FOUND_RESOURCE_PATH));
    }

    public String getQueryValue(String key) {
        if (!queryString.containsKey(key)) {
            throw new IllegalArgumentException("키가 존재하지 않습니다.");
        }
        return queryString.get(key);
    }

    public URL getUrl() {
        return url;
    }

    public Map<String, String> getQueryString() {
        return queryString;
    }

    public boolean isSamePath(String urlPath) {
        return Objects.equals(this.path, urlPath);
    }
}
