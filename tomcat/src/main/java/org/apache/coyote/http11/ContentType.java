package org.apache.coyote.http11;

public enum ContentType {

    HTML("text/html", ".html"),
    JS("text/javascript", ".js"),
    CSS("text/css", ".css"),
    SVG("image/svg+xml", ".svg"),
    PLAIN("text/plain", ""),
    ;

    private final String name;
    private final String extension;

    ContentType(String name, String extension) {
        this.name = name;
        this.extension = extension;
    }

    public static ContentType determineContentType(String resourcePath) {
        for (ContentType contentType : ContentType.values()) {
            if (resourcePath.endsWith(contentType.getExtension())) {
                return contentType;
            }
        }

        return ContentType.PLAIN;
    }

    public String getName() {
        return name + ";charset=utf-8";
    }

    public String getExtension() {
        return extension;
    }
}
