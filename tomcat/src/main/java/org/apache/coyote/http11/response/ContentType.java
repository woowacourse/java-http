package org.apache.coyote.http11.response;

public enum ContentType {

    HTML("text/html"),
    CSS("text/css"),
    JAVASCRIPT("text/javascript"),
    SVG("image/svg+xml");

    private final String value;

    ContentType(String value) {
        this.value = value;
    }

    String value() {
        return value;
    }

    public static ContentType fromResourceName(String resourceName) {
        final int extensionSeparator = resourceName.lastIndexOf('.');
        final String extension = resourceName.substring(extensionSeparator + 1);

        return switch (extension) {
            case "css" -> CSS;
            case "js" -> JAVASCRIPT;
            case "svg" -> SVG;
            case "html" -> HTML;
            default -> HTML;
        };
    }
}
