package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.List;

public enum ContentType {

    TEXT_HTML(List.of("html"), "text/html;charset=utf-8"),
    TEXT_CSS(List.of("css"), "text/css"),
    TEXT_JAVASCRIPT(List.of("js"), "text/javascript"),
    TEXT_PLAIN(List.of("txt"), "text/plain"),
    IMAGE_SVG_XML(List.of("svg"), "image/svg+xml");

    private final List<String> fileExtensions;
    private final String mimeType;

    ContentType(List<String> fileExtensions, String mimeType) {
        this.fileExtensions = fileExtensions;
        this.mimeType = mimeType;
    }

    public static ContentType of(String fileExtension) {
        return Arrays.stream(ContentType.values())
                .filter(type -> type.fileExtensions.contains(fileExtension))
                .findFirst().orElseThrow(() -> new RuntimeException("잘못된 파일 확장자입니다."));
    }

    public String getMimeType() {
        return mimeType;
    }
}
