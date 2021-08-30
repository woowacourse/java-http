package nextstep.jwp;

import java.util.Arrays;

public enum ContentType {

    HTML("html", "text/html"),
    CSS("css", "text/css"),
    JS("js", "text/javascript"),
    ICO("ico", "image/x-icon"),
    SVG("svg", "image/svg+xml"),
    NONE("none", "text/html");

    private final String fileExtension;
    private final String contentType;

    ContentType(String fileExtension, String contentType) {
        this.fileExtension = fileExtension;
        this.contentType = contentType;
    }

    public static ContentType findBy(String resource) {
        return Arrays.stream(values())
                .filter(val -> resource.endsWith(val.fileExtension))
                .findFirst()
                .orElse(NONE);
    }

    public String getContentType() {
        return contentType;
    }
}
