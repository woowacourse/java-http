package nextstep.jwp.http;

public enum ContentType {
    HTML("text/html;charset=utf-8"),
    CSS("text/css"),
    JS("application/javascript; charset=UTF-8"),
    IMAGE("image/svg+xml");

    private final String type;

    ContentType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
