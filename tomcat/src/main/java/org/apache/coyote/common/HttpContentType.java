package org.apache.coyote.common;

public enum HttpContentType {
    TEXT_PLAIN("text/plain", "."),
    TEXT_HTML("text/html;charset=utf-8", ".html"),
    TEXT_CSS("text/css", ".css"),
    APPLICATION_JAVASCRIPT("application/javascript", ".js"),
    IMAGE_SVG("image/svg+xml", ".svg"),
    ;

    private final String value;
    private final String extension;

    HttpContentType(String value, String extension) {
        this.value = value;
        this.extension = extension;
    }

    public static HttpContentType from(String extension) {
        if (extension == null) {
            return TEXT_PLAIN;
        }
        for (HttpContentType contentType : values()) {
            if (extension.endsWith(contentType.getExtension())) {
                return contentType;
            }
        }
        return TEXT_PLAIN;
    }

    public String getValue() {
        return value;
    }

    public String getExtension() {
        return extension;
    }
}
