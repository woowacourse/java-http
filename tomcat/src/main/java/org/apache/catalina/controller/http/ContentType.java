package org.apache.catalina.controller.http;

import java.util.Arrays;

public enum ContentType {

    HTML("text/html;charset=utf-8", "html"),
    CSS("text/css", "css"),
    JS("application/javascript", "js");

    private final String value;
    private final String resourceType;

    ContentType(String value, String resourceType) {
        this.value = value;
        this.resourceType = resourceType;
    }

    public static ContentType of(String resourceType) {
        return Arrays.stream(values())
                .filter(contentType -> contentType.resourceType.equals(resourceType))
                .findFirst()
                .orElse(HTML);
    }

    public String getValue() {
        return value;
    }

    public String getResourceType() {
        return resourceType;
    }
}
