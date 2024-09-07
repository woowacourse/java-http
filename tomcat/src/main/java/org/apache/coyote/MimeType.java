package org.apache.coyote;

import java.util.Arrays;
import org.apache.coyote.util.FileExtension;

public enum MimeType {

    HTML("text/html", CharsetType.UTF_8, FileExtension.HTML),
    CSS("text/css", CharsetType.NONE, FileExtension.CSS),
    JS("text/javascript", CharsetType.NONE, FileExtension.JS),
    ICO("image/x-ico", CharsetType.NONE, FileExtension.ICO),
    PNG("image/png", CharsetType.NONE, FileExtension.PNG),
    JPG("image/jpeg", CharsetType.NONE, FileExtension.JPG),
    SVG("image/svg+xml", CharsetType.UTF_8, FileExtension.SVG),
    OTHER("text/plain", CharsetType.NONE, null),
    ;

    private final String mimeType;
    private final CharsetType charset;
    private final FileExtension extension;

    MimeType(String mimeType, CharsetType charset, FileExtension extension) {
        this.mimeType = mimeType;
        this.charset = charset;
        this.extension = extension;
    }

    public static MimeType from(FileExtension fileExtension) {
        return Arrays.stream(MimeType.values())
                .filter(mime -> mime.extension != null && mime.extension == fileExtension)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("허용되지 않는 타입입니다. type = " + fileExtension));
    }

    public String getContentType() {
        if (charset == CharsetType.NONE) {
            return mimeType;
        }
        return mimeType + ";charset=" + charset.getCharset();
    }
}
