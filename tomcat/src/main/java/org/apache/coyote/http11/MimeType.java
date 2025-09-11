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

    public static MimeType fromMimeTypeString(String mimeTypeString) {
        for (MimeType mimeType : values()) {
            if (mimeType.type.equalsIgnoreCase(mimeTypeString.trim())) {
                return mimeType;
            }
        }
        return null;
    }

    public static MimeType fromExtensionString(String extensionString) {
        for (MimeType mimeType : values()) {
            if (mimeType.extension.equalsIgnoreCase(extensionString.trim())) {
                return mimeType;
            }
        }
        return null;
    }

    public String getType() {
        return type;
    }

    public String getExtension() {
        return extension;
    }
}
