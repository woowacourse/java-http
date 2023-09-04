package nextstep.jwp.common;

public enum ContentType {
    HTML("text/html;charset=utf-8"),
    CSS("text/css"),
    JS("Application/javascript"),
    SVG("image/svg+xml");

    private final String type;

    ContentType(final String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
