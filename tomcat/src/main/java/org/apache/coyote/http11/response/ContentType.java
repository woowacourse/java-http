package org.apache.coyote.http11.response;

import java.util.Arrays;
import org.apache.coyote.http11.utils.UrlParser;

public enum ContentType {
    TEXT_HTML("text/html", "html"),
    TEXT_CSS("text/css", "css"),
    TEXT_JS("text/js", "js"),
    IMAGE_CSV("image/svg+xml", "svg"),
    IMAGE_ICO("image/x-icon", "ico"),
    ;

    private final String name;
    private final String extension;

    ContentType(String name, String extension) {
        this.name = name;
        this.extension = extension;
    }

    public static String from(String url) {
        String path = UrlParser.convertEmptyToHtml(url);
        return Arrays.stream(values())
                .filter(contentType -> path.endsWith(contentType.extension))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("알맞은 확장자가 없습니다."))
                .name;
    }

    public String getExtension() {
        return extension;
    }
}
