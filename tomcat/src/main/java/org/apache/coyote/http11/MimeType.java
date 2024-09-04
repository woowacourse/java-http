package org.apache.coyote.http11;

import org.apache.coyote.util.FileExtension;

public enum MimeType {

    HTML("text/html;charset=utf-8", FileExtension.HTML),
    CSS("text/css", FileExtension.CSS),
    JS("text/javascript", FileExtension.JS),
    ICO("image/x-ico", FileExtension.ICO),
    PNG("image/png", FileExtension.PNG),
    JPG("image/jpeg", FileExtension.JPG),
    SVG("image/svg+xml", FileExtension.SVG),
    OTHER("text/plain", null),
    ;

    private final String value;
    private final FileExtension extension;

    MimeType(String value, FileExtension extension) {
        this.value = value;
        this.extension = extension;
    }

    public static MimeType from(FileExtension fileExtension) {
        for (MimeType mime : values()) {
            if (mime.extension != null && fileExtension == mime.extension) {
                return mime;
            }
        }
        throw new IllegalArgumentException();
    }

    public String getValue() {
        return value;
    }
}
