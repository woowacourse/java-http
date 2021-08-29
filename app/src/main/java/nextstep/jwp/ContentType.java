package nextstep.jwp;

public enum ContentType {
    HTML("text/html;charset=utf-8"),
    CSS("text/css"),
    JAVASCRIPT("application/javascript; charset=UTF-8"),
    IMAGE("image/svg+xml");

    private String contentType;

    ContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getContentType() {
        return contentType;
    }
}
