package org.apache.coyote.http.response;

import java.util.Arrays;

public enum ContentType {

    TEXT_HTML(".html", "text/html", "text/html;charset=utf-8"),
    CSS(".css", "text/css", "text/css"),
    JS(".js", "text/javascript", "text/javascript;charset=utf-8"),
    ICO(".ico", "image/apng", "image/apng"),
    JSON(".json", "application/json", "application/json");

    private final String extension;
    private final String accept;
    private final String content;

    ContentType(final String extension, final String accept, final String content) {
        this.extension = extension;
        this.accept = accept;
        this.content = content;
    }

    public static ContentType findContentType(final String... targetType) {
        return Arrays.stream(ContentType.values())
                .filter(contentType -> hasType(contentType, targetType))
                .findAny()
                .orElse(ContentType.JSON);
    }

    private static boolean hasType(final ContentType contentType, final String[] target) {
        return Arrays.stream(target).anyMatch(type ->
                type.endsWith(contentType.extension) || type.contains(contentType.accept));
    }

    public String getContent() {
        return content;
    }
}
