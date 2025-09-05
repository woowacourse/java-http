package com.techcourse.web.ui;

public enum ContentType {
    HTML( ".html","text/html"),
    CSS( ".css","text/css"),
    JS(".js","application/javascript"),
    JSON(".json","application/json"),
    FAVICON(".ico","image/x-icon");

    private final String extension;
    private final String contentType;

    ContentType(final String extension, final String contentType) {
        this.extension = extension;
        this.contentType = contentType;
    }

    public String getExtension() {
        return extension;
    }

    public String getContentType() {
        return contentType;
    }

    public static String findContentType(String path) {
        for (ContentType type : values()) {
            if(path.endsWith(type.extension)){
                return type.getContentType();
            }
        }
        return "text/plain";
    }
}
