package org.apache.coyote.http11;

public enum MimeType {

    HTML("text/html", "html"),
    CSS("text/css", "css"),
    JS("application/javascript", "js"),
    JSON("application/json", "json"),
    PNG("image/png", "png"),
    JPEG("image/jpeg", "jpeg"),
    JPG("image/jpeg", "jpg"),
    GIF("image/gif", "gif"),
    SVG("image/svg+xml", "svg"),
    ICO("image/x-icon", "ico"),
    TXT("text/plain", "txt"),
    XML("application/xml", "xml"),
    PDF("application/pdf", "pdf"),
    ZIP("application/zip", "zip"),
    FORM_URLENCODED("application/x-www-form-urlencoded", "urlencoded"),
    MULTIPART_FORM_DATA("multipart/form-data", "multipart"),
    ;

    private final String type;
    private final String extension;

    MimeType(String type, String extension) {
        this.type = type;
        this.extension = extension;
    }

    public String getType() {
        if (this == HTML || this == CSS || this == JS || this == JSON || this == TXT || this == XML) {
            return type + ";charset=UTF-8";
        }
        return type;
    }

    public String getExtension() {
        return extension;
    }

    public static MimeType getOrDefault(String extension) {
        for (MimeType mimeType : values()) {
            if (mimeType.getExtension().equalsIgnoreCase(extension)) {
                return mimeType;
            }
        }
        return HTML;
    }
}
