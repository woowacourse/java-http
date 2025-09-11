package org.apache.coyote.http11.response.header;

import java.util.Arrays;
import org.apache.coyote.http11.exception.InternalServerErrorException;

public enum MimeType {

    HTML("html", "text/html;charset=utf-8"),
    CSS("css", "text/css;charset=utf-8"),
    JS("js", "text/javascript;charset=utf-8");

    private final String extension;
    private final String mimeType;

    MimeType(final String extension, final String mimeType) {
        this.extension = extension;
        this.mimeType = mimeType;
    }

    public static MimeType of(final String givenExtension) {
        return Arrays.stream(MimeType.values())
                .filter(type -> type.extension.equals(givenExtension))
                .findFirst()
                .orElseThrow(InternalServerErrorException::new);
    }

    public String getMimeType() {
        return mimeType;
    }
}
