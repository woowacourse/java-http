package nextstep.jwp.util;

import java.util.Arrays;

public enum StaticResources {
    JS("js"),
    CSS("css"),
    HTML("html"),
    FAVICON("ico");

    private final String resource;

    StaticResources(String resource) {
        this.resource = resource;
    }

    public static boolean matchFromHeader(HeaderLine headerLine) {
        if (!headerLine.isResource()) {
            return false;
        }
        String fileFormat = headerLine.resourceType();

        return matchType(fileFormat);
    }

    public static boolean matchType(String fileFormat) {
        return Arrays.stream(StaticResources.values())
            .anyMatch(element -> element.isSameFileFormat(fileFormat));
    }

    private boolean isSameFileFormat(String fileFormat) {
        return this.resource.equals(fileFormat);
    }
}
