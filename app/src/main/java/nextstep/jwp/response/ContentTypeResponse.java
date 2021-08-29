package nextstep.jwp.response;

import java.util.Arrays;

public enum ContentTypeResponse {

    HTML("Content-Type: text/html;charset=utf-8 "),
    CSS("Content-Type: text/css"),
    JS("Content-Type: application/javascript"),
    SVG("Content-Type: image/svg+xml"),
    OTHER("");

    private final String contentTypeHeader;

    ContentTypeResponse(String contentTypeHeader) {
        this.contentTypeHeader = contentTypeHeader;
    }

    public static ContentTypeResponse of(String fileType) {
        return Arrays.stream(values())
                .filter(value -> value.name().equalsIgnoreCase(fileType))
                .findAny()
                .orElse(OTHER);
    }

    public String getContentTypeHeader() {
        return contentTypeHeader;
    }
}
