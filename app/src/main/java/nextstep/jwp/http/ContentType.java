package nextstep.jwp.http;

import java.util.Arrays;

public enum ContentType {

    HTML("html", "text/html; charset=utf-8"),
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

    public boolean isNone() {
        return NONE.equals(this);
    }

    public boolean isHtml() {
        return HTML.equals(this);
    }

    public String getContentType() {
        return contentType;
    }
}
