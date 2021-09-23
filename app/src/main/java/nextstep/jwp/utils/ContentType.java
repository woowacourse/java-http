package nextstep.jwp.utils;

import java.util.Arrays;

public enum ContentType {

    HTML("html", "text/html; charset=utf-8"),
    CSS("css", "text/css"),
    JS("js", "text/javascript"),
    ICO("ico", "image/x-icon"),
    SVG("svg", "image/svg+xml"),
    NONE("none", "text/html");

    private final String fileExtension;
    private final String type;

    ContentType(String fileExtension, String type) {
        this.fileExtension = fileExtension;
        this.type = type;
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

    public String getType() {
        return type;
    }
}
