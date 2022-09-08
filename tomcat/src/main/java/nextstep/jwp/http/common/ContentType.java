package nextstep.jwp.http.common;

public enum ContentType {

    TEXT_HTML("html", "text/html"),
    TEXT_CSS("css", "text/css"),
    APPLICATION_JAVASCRIPT("js", "text/javascript"),
    IMAGE_X_ICON("ico", "image/x-icon"),
    ;

    private final String extension;
    private final String type;

    ContentType(final String extension, final String type) {
        this.extension = extension;
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
