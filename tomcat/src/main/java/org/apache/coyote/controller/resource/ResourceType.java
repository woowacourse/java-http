package org.apache.coyote.controller.resource;

import java.util.Arrays;

public enum ResourceType {

    HTML(".html", "text/html;charset=utf-8"),
    CSS(".css", "text/css;charset=utf-8"),
    JS(".js", "application/javascript;charset=utf-8"),
    SVG(".svg","image/svg+xml");

    private final String extension;
    private final String type;

    ResourceType(
            final String extension,
            final String type
    ) {
        this.extension = extension;
        this.type = type;
    }

    public static ResourceType findResourceType(final String path) {
        return Arrays.stream(ResourceType.values())
                .filter(resourceType -> path.endsWith(resourceType.extension))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않은 형식입니다."));
    }

    public static boolean isStaticRequest(final String path) {
        return Arrays.stream(ResourceType.values())
                .anyMatch(resourceType -> path.endsWith(resourceType.extension));
    }

    public String getType() {
        return type;
    }
}
