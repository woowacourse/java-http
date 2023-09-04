package org.apache.coyote.response;

import java.util.Arrays;

public enum ResourceType {
    HTML("text/html"),
    CSS("text/css"),
    ALL("*/*");

    private final String resourceType;

    ResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public static ResourceType findResourceType(String resourceType) {
        return Arrays.stream(values())
                .filter(resource -> resource.getResourceType().equals(resourceType))
                .findFirst()
                .orElse(HTML);
    }

    public String getResourceType() {
        return resourceType;
    }
}
