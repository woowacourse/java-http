package org.apache.coyote.response;

import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

public class Resource {

    private static final String NOT_FOUND_RESOURCE_PATH = "/static/404.html";

    private final URL url;
    private final List<ResourceType> resourceTypes;
    private final boolean isExists;

    public static Resource of(URL url, List<ResourceType> resourceType) {
        if (url == null) {
            return new Resource(Resource.class.getResource(NOT_FOUND_RESOURCE_PATH), resourceType, false);
        }
        return new Resource(url, resourceType, true);
    }

    private Resource(URL url, List<ResourceType> resourceTypes, boolean isExists) {
        this.url = url;
        this.resourceTypes = resourceTypes;
        this.isExists = isExists;
    }

    public String getResourceTypes() {
        return resourceTypes.stream()
                .map(ResourceType::getResourceType)
                .collect(Collectors.joining(","));
    }

    public URL getUrl() {
        return url;
    }

    public boolean isExists() {
        return isExists;
    }
}
