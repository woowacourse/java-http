package http;

public enum HttpHeaderKey {

    CONTENT_LENGTH("Content-Length"),
    CONTENT_TYPE("Content-Type"),
    ;

    private final String value;

    HttpHeaderKey(final String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
