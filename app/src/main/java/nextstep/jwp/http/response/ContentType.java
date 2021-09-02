package nextstep.jwp.http.response;

import java.util.Arrays;

public enum ContentType {
    HTML("html", "text/html"),
    CSS("css", "text/css"),
    JS("js", "application/js"),
    ICO("ico", "image/x-icon"),
    SVG("svg", "image/svg+xml");

    private final String fileNameExtension;
    private final String value;

    ContentType(String fileNameExtension, String value) {
        this.fileNameExtension = fileNameExtension;
        this.value = value;
    }

    public static ContentType getContentTypeByFileNameExtension(String fileNameExtension) {
        return Arrays.stream(ContentType.values())
                .filter(type -> type.hasSameFileNameExtension(fileNameExtension))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("파일 확장자가 존재하지 않습니다."));
    }

    private boolean hasSameFileNameExtension(String fileNameExtension) {
        return this.fileNameExtension.equals(fileNameExtension);
    }

    public String getValue() {
        return value;
    }
}
