package org.apache.coyote;

import java.util.Arrays;
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

    private final String contentType;
    private final FileExtension extension;

    MimeType(String contentType, FileExtension extension) {
        this.contentType = contentType;
        this.extension = extension;
    }

    public static MimeType from(FileExtension fileExtension) {
        return Arrays.stream(MimeType.values())
                .filter(mime -> mime.extension != null && mime.extension == fileExtension)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("허용되지 않는 타입입니다. type = " + fileExtension));
    }

    public String getContentType() {
        return contentType;
    }
}
