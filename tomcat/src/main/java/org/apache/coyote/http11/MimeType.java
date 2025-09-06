package org.apache.coyote.http11;

import com.techcourse.exception.BadRequestException;
import java.util.Arrays;

public enum MimeType {

    HTML(".html", "text/html;charset=utf-8"),
    CSS(".css", "text/css;charset=utf-8"),
    JS(".js", "application/javascript;charset=utf-8"),
    JSON(".json", "application/json;charset=utf-8"),
    SVG(".svg", "image/svg+xml"),
    PNG(".png", "image/png"),
    JPG(".jpg", "image/jpeg"),
    JPEG(".jpeg", "image/jpeg"),
    GIF(".gif", "image/gif"),
    ICO(".ico", "image/x-icon"),
    TXT(".txt", "text/plain;charset=utf-8"),
    DEFAULT("", "application/octet-stream");

    private static final String DOT = ".";

    private final String extension;
    private final String mimeType;

    MimeType(String extension, String mimeType) {
        this.extension = extension;
        this.mimeType = mimeType;
    }

    public static String fromPath(final String path) {
        String lowerCasePath = path.toLowerCase();
        int dotIndex = lowerCasePath.indexOf(DOT);
        final String extension = parseExtension(dotIndex, lowerCasePath);

        return Arrays.stream(MimeType.values())
                .filter(mimeType1 -> mimeType1.extension.equals(extension))
                .map(MimeType::mimeType)
                .findFirst()
                .orElseThrow(() -> new BadRequestException("MimeType does not exists"));
    }

    private static String parseExtension(final int dotIndex, final String lowerCasePath) {
        String extension = "";
        if (dotIndex > 0) {
            extension = lowerCasePath.substring(dotIndex);
        }
        return extension;
    }

    public String extension() {
        return extension;
    }

    public String mimeType() {
        return mimeType;
    }
}
