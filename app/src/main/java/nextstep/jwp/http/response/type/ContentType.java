package nextstep.jwp.http.response.type;

import java.util.Arrays;

public enum ContentType {

    HTML("html", "text/html;charset=utf-8"),
    CSS("css", "text/css"),
    JS("js", "application/javascript"),
    SVG("svg", "image/svg+xml"),
    ICO("ICO", "image/x-icon");

    private final String extension;
    private final String value;

    ContentType(String extension, String value) {
        this.extension = extension;
        this.value = value;
    }

    public static ContentType findByUri(String uri) {
        String extension = uri.split("\\.")[1];
        return Arrays.stream(ContentType.values())
                .filter(contentType -> contentType.getExtension().equals(extension))
                .findAny().orElseThrow(() -> new IllegalArgumentException("해당하는 ContentType을 찾을 수 없습니다."));
    }

    public String getExtension() {
        return extension;
    }

    public String getValue() {
        return value;
    }
}
