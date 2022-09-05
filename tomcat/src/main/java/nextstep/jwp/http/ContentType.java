package nextstep.jwp.http;

import java.util.Arrays;

public enum ContentType {

    TEXT_PLAIN("txt", "text", "plain"),
    TEXT_HTML("html", "text", "html"),
    TEXT_CSS("css", "text", "css"),
    APPLICATION_JAVASCRIPT("js", "application", "javascript"),
    IMAGE_X_ICON("ico", "image", "x-icon"),
    SVG("svg", "image", "svg+xml"),
    ;

    private static final String MEDIA_TYPE_FORMAT = "%s/%s";

    private final String fileExtension;
    private final String type;
    private final String subType;

    ContentType(String fileExtension, String type, String subType) {
        this.fileExtension = fileExtension;
        this.type = type;
        this.subType = subType;
    }

    public static ContentType from(String fileExtension) {
        return Arrays.stream(values())
            .filter(it -> it.fileExtension.equals(fileExtension))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("올바르지 않은 확장자입니다."));
    }

    public String getMediaType() {
        return String.format(MEDIA_TYPE_FORMAT, type, subType);
    }

    public String getFileExtension() {
        return this.fileExtension;
    }
}
