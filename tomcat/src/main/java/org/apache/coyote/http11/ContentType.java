package org.apache.coyote.http11;

import java.util.Arrays;
import org.apache.coyote.http11.exception.ContentNotFoundException;

public enum ContentType {

    HTML("html", "text/html"),
    CSS("css", "text/css"),
    JS("js", "text/javascript");

    private final String extension;
    private final String MIMEType;

    ContentType(final String extension, final String mimeType) {
        this.extension = extension;
        this.MIMEType = mimeType;
    }

    public static ContentType matchMIMEType(String extension) {
        return Arrays.stream(values())
                .filter(type -> type.extension.equals(extension))
                .findAny()
                .orElseThrow(ContentNotFoundException::new);
    }

    public String getMIMEType() {
        return MIMEType;
    }
}
