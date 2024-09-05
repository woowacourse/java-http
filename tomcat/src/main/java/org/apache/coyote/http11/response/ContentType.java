package org.apache.coyote.http11.response;

import java.util.Arrays;

enum ContentType {
    HTML("text/html"),
    CSS("text/css"),
    JS("text/js");

    private final String rawContentType;

    static ContentType[] fromResourceName(String resourceName) {
        String[] split = resourceName.split("\\.");
        if (split.length == 1) {
            return new ContentType[]{HTML};
        }

        String fileExtension = split[1];
        ContentType[] result = findContentTypes(fileExtension);

        if (result.length == 0) {
            return new ContentType[]{HTML};
        }
        return result;
    }

    private static ContentType[] findContentTypes(String fileExtension) {
        return Arrays.stream(values())
                .filter(contentType -> contentType.rawContentType.contains(fileExtension))
                .toArray(ContentType[]::new);
    }

    ContentType(String rawContentType) {
        this.rawContentType = rawContentType;
    }

    public String getRawContentType() {
        return rawContentType;
    }
}
