package nextstep.jwp.httpmessage;

public enum ContentType {

    HTML("text/html;charset=utf-8"),
    CSS("text/css"),
    JAVASCRIPT("application/javascript; charset=UTF-8"),
    X_WWW_FORM("application/x-www-form-urlencoded"),
    IMAGE("image/svg+xml");

    private String contentType;

    ContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getContentType() {
        return contentType;
    }
}
