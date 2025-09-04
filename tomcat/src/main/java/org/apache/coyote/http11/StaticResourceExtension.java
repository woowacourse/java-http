package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.List;

public enum StaticResourceExtension {

    HTML("html", "text/html", List.of("static")),
    CSS("css", "text/css", List.of("static/css")),
    JS("js", "application/javascript", List.of("static/js", "static/assets")),
    SVG("svg", "image/svg+xml", List.of("static/assets/img")),
    ;

    private final String extension;
    private final String mimeType;
    private final List<String> paths;

    StaticResourceExtension(String extension, String mimeType, List<String> paths) {
        this.extension = extension;
        this.mimeType = mimeType;
        this.paths = paths;
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

    public static List<String> findPathsByUrl(String url) {
        return Arrays.stream(StaticResourceExtension.values())
                .filter(staticResourceExtension -> url.contains(staticResourceExtension.getExtension()))
                .findFirst()
                .map(StaticResourceExtension::getPaths)
                .orElseThrow(() -> new IllegalArgumentException("일치하는 경로가 존재하지 않는 확장명입니다."));
    }

    public String getExtension() {
        return extension;
    }

    public String getMimeType() {
        return mimeType;
    }

    public List<String> getPaths() {
        return paths;
    }
}
