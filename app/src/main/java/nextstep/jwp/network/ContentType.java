package nextstep.jwp.network;

public enum ContentType {
    HTML("text/html;charset=utf-8"),
    CSS("text/css"),
    JAVASCRIPT("application/x-javascript"),
    IMAGE("image/x-icon");

    private final String type;

    ContentType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
