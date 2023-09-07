package org.apache.coyote.http11.response;

import java.util.Arrays;
import java.util.Objects;

public enum HttpContentType {
    TEXT_HTML("text/html", "html"),
    TEXT_CSS("text/css", "css"),
    TEXT_JAVASCRIPT("text/javascript", "js"),
    TEXT_PLAIN("text/plain", "text"),
    IMAGE_SVG("image/svg+xml", "svg"),
    ;

    private static final String DELIMITER = ";";

    private final String mimeType;
    private final String fileExtension;

    HttpContentType(String mimeType, String fileExtension) {
        this.mimeType = mimeType;
        this.fileExtension = fileExtension;
    }

    public static HttpContentType getByFileExtension(String fileExtension) {
        return Arrays.stream(values())
                .filter(httpContentType -> Objects.equals(httpContentType.fileExtension, fileExtension))
                .findAny()
                .orElse(TEXT_PLAIN);
    }

    public String mimeTypeWithCharset(Charset charset) {
        return String.join(DELIMITER, mimeType, charset.charsetWithFiled());
    }

    public String mimeType() {
        return mimeType;
    }

    public String fileExtension() {
        return fileExtension;
    }
}
