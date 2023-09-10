package org.apache.coyote.http11.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum ContentType {

    HTML("html", "text/html"),
    CSS("css", "text/css"),
    JAVASCRIPT("js", "text/javascript"),
    SVG("svg", "image/svg+xml")
    ;

    private final String extension;
    private final String name;

    public static ContentType from(String contentName) {
        return Arrays.stream(ContentType.values())
                .filter(it -> contentName.endsWith(it.extension.toLowerCase()))
                .findFirst()
                .orElse(HTML);
    }
}
