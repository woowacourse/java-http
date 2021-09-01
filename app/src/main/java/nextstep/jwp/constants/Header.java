package nextstep.jwp.constants;

public enum Header {
    CONTENT_LENGTH("Content-Length"),
    LOCATION("Location"),
    CONTENT_TYPE("Content-Type");

    private final String key;

    Header(String key) {
        this.key = key;
    }

    public String getKey() {
        return this.key;
    }
}
