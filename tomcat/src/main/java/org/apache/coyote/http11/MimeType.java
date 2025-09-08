package org.apache.coyote.http11;

public enum MimeType {

    CSS("css", "text/css"),
    HTML("html", "text/html"),
    JSON("json", "application/json"),
    JAVASCRIPT("js", "application/javascript");

    private final String extension;
    private final String type;

    MimeType(String extension, String type) {
        this.extension = extension;
        this.type = type;
    }

    public static MimeType fromMimeTypeString(String type) {
        for (MimeType mimeType : values()) {
            if (mimeType.type.equalsIgnoreCase(type)) {
                return mimeType;
            }
        }
        return null;
    }

    public static MimeType fromExtension(String extension) {
        for (MimeType mimeType : values()) {
            if (mimeType.extension.equalsIgnoreCase(extension)) {
                return mimeType;
            }
        }
        return null;
    }

    public String getExtension() {
        return extension;
    }

    public String getType() {
        return type;
    }
}
