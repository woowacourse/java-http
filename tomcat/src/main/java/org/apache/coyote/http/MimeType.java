package org.apache.coyote.http;

import com.techcourse.exception.UncheckedServletException;
import java.util.Arrays;

public enum MimeType {

    HTML("text/html"),
    CSS("text/css"),
    JS("application/javascript"),
    JSON("application/json"),
    XML("application/xml"),
    JPEG("image/jpeg"),
    PNG("image/png"),
    GIF("image/gif"),
    TEXT("text/plain"),
    PDF("application/pdf");

    private final String contentType;

    MimeType(String contentType) {
        this.contentType = contentType;
    }

    public static MimeType from(String fileName) {
        return Arrays.stream(MimeType.values())
                .filter(mimeType -> fileName.endsWith(mimeType.name().toLowerCase()))
                .findAny()
                .orElseThrow(() -> new UncheckedServletException("지원하지 않는 파일입니다."));
    }

    public String getContentType() {
        return contentType;
    }
}
