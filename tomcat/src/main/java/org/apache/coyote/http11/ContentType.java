package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.NoSuchElementException;

public enum ContentType {

    HTML("text/html", ".html"),
    JS("text/javascript", ".js"),
    CSS("text/css", ".css"),
    SVG("image/svg+xml", ".svg"),
    PLAIN("text/plain", ""),
    APPLICATION_X_WWW_FORM_URL_ENCODED("application/x-www-form-urlencoded", ""),
    ;

    private final String name;
    private final String extension;

    ContentType(String name, String extension) {
        this.name = name;
        this.extension = extension;
    }

    public static ContentType from(String contentTypeName) {
        return Arrays.stream(values())
                .filter(it -> it.name.equals(contentTypeName))
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
    }

    public static ContentType determineContentType(String resourcePath) {
        for (ContentType contentType : ContentType.values()) {
            if (resourcePath.endsWith(contentType.getExtension())) {
                return contentType;
            }
        }

        return ContentType.PLAIN;
    }

    public boolean isApplicationXW3FormUrlEncoded() {
        return this.equals(APPLICATION_X_WWW_FORM_URL_ENCODED);
    }

    public String getName() {
        return name + ";charset=utf-8";
    }

    public String getExtension() {
        return extension;
    }
}
