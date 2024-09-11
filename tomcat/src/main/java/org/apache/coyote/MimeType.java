package org.apache.coyote;

import java.util.Arrays;
import org.apache.coyote.util.FileExtension;

public enum MimeType {

    HTML("text/html", FileExtension.HTML),
    CSS("text/css", FileExtension.CSS),
    JS("text/javascript", FileExtension.JS),
    ICO("image/x-ico", FileExtension.ICO),
    PNG("image/png", FileExtension.PNG),
    JPG("image/jpeg", FileExtension.JPG),
    SVG("image/svg+xml", FileExtension.SVG),
    OTHER("text/plain", FileExtension.NONE),
    ;

    private static final String TEXT = "text/";
    private final String mimeType;
    private final FileExtension extension;

    MimeType(String mimeType, FileExtension extension) {
        this.mimeType = mimeType;
        this.extension = extension;
    }

    public static MimeType from(FileExtension fileExtension) {
        return Arrays.stream(MimeType.values())
                .filter(mime -> mime.extension != null && mime.extension == fileExtension)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("허용되지 않는 타입입니다. type = " + fileExtension));
    }

    public boolean isTextBased() {
        return mimeType.startsWith(TEXT);
    }

    public String getMimeType() {
        return mimeType;
    }
}
