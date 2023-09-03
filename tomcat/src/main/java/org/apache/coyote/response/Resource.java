package org.apache.coyote.response;

import java.net.URL;

public class Resource {

    private static final String NOT_FOUND_RESOURCE_PATH = "/static/404.html";

    private final URL url;
    private final ResourceType resourceType;
    private final boolean isExists;

    public Resource of(URL url, ResourceType resourceType) {
        if (url == null) {
            return new Resource(getClass().getResource(NOT_FOUND_RESOURCE_PATH), resourceType, false);
        }
        return new Resource(url, resourceType, true);
    }

    private Resource(URL url, ResourceType resourceType, boolean isExists) {
        this.url = url;
        this.resourceType = resourceType;
        this.isExists = isExists;
    }

    public URL getUrl() {
        return url;
    }

    public ResourceType getResourceType() {
        return resourceType;
    }

    public boolean isExists() {
        return isExists;
    }
}
