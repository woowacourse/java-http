package org.apache.coyote.request;

import java.net.URL;
import java.util.Map;

public class Request {

    private final RequestUrl url;
    private final RequestContentType requestContentType;

    public Request(RequestUrl url, RequestContentType requestContentType) {
        this.url = url;
        this.requestContentType = requestContentType;
    }

    public String getResourceTypes() {
        return requestContentType.getContentType();
    }

    public String getQueryStringValue(String key) {
        return url.getQueryValue(key);
    }

    public boolean isSamePath(String urlPath) {
        return url.isSamePath(urlPath);
    }

    public URL getUrl() {
        return url.getUrl();
    }

    public Map<String, String> getQueryString() {
        return url.getQueryString();
    }

    public boolean isExists() {
        return !url.isNullPath();
    }
}
