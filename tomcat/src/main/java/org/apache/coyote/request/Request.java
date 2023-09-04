package org.apache.coyote.request;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Request {

    private final RequestUrl url;
    private final List<RequestContentType> requestContentTypes;

    public static Request of(RequestUrl url, List<RequestContentType> requestContentType) {
        return new Request(url, requestContentType);
    }

    private Request(RequestUrl url, List<RequestContentType> requestContentTypes) {
        this.url = url;
        this.requestContentTypes = requestContentTypes;
    }

    public String getResourceTypes() {
        return requestContentTypes.stream()
                .map(RequestContentType::getContentType)
                .collect(Collectors.joining(","));
    }

    public String getQueryStringValue(String key) {
        return url.getQueryValue(key);
    }

    public URL getPath() {
        return url.getPath();
    }

    public Map<String, String> getQueryString() {
        return url.getQueryString();
    }

    public boolean isExists() {
        return !url.isNullPath();
    }
}
