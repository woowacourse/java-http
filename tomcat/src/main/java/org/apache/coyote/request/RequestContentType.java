package org.apache.coyote.request;

import java.util.Arrays;

public enum RequestContentType {
    HTML("text/html"),
    CSS("text/css"),
    ALL("*/*");

    private final String contentType;

    RequestContentType(String contentType) {
        this.contentType = contentType;
    }

    public static RequestContentType findResourceType(String resourceType) {
        return Arrays.stream(values())
                .filter(resource -> resource.getContentType().equals(resourceType))
                .findFirst()
                .orElse(HTML);
    }

    public String getContentType() {
        return contentType;
    }
}
