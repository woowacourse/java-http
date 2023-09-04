package org.apache.coyote.response;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.coyote.request.RequestUrl;

public class Resource {

    private final RequestUrl url;
    private final List<ResourceType> resourceTypes;

    public static Resource of(RequestUrl url, List<ResourceType> resourceType) {
        return new Resource(url, resourceType);
    }

    private Resource(RequestUrl url, List<ResourceType> resourceTypes) {
        this.url = url;
        this.resourceTypes = resourceTypes;
    }

    public String getResourceTypes() {
        return resourceTypes.stream()
                .map(ResourceType::getResourceType)
                .collect(Collectors.joining(","));
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
