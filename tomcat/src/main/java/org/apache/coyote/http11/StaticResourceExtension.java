package org.apache.coyote.http11;

import java.util.Arrays;

public enum StaticResourceExtension {

    HTML("html", "text/html"),
    CSS("css", "text/css"),
    JS("js", "application/javascript"),
    SVG("svg", "image/svg+xml");

    private final String extension;
    private final String mimeType;

    StaticResourceExtension(String extension, String mimeType) {
        this.extension = extension;
        this.mimeType = mimeType;
    }

    public static boolean anyMatch(String comparedExtension) {
        return Arrays.stream(StaticResourceExtension.values())
                .anyMatch(
                        staticResourceExtension -> comparedExtension.contains(staticResourceExtension.getExtension()));
    }

    public static String findMimeTypeByUrl(String url) {
        return Arrays.stream(StaticResourceExtension.values())
                .filter(staticResourceExtension -> url.contains(staticResourceExtension.getExtension()))
                .findFirst()
                .map(StaticResourceExtension::getMimeType)
                .orElseThrow(() -> new IllegalArgumentException("일치하는 MIME TYPE 이 존재하지 않는 확장명입니다."));
    }

    public String getExtension() {
        return extension;
    }

    public String getMimeType() {
        return mimeType;
    }
}
