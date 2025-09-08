package org.apache.coyote.http11;

import java.util.Arrays;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ContentType {
    HTML(".html", "text/html"),
    CSS(".css", "text/css"),
    SVG(".svg", "image/svg+xml"),
    JS(".js", "application/javascript"),
    ;

    private final String extension;
    private final String mimeType;

    public static ContentType from(String path) {
        return Arrays.stream(values())
                .filter(type -> path.endsWith(type.extension))
                .findFirst()
                .orElse(HTML);
    }
}
