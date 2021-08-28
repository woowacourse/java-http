package nextstep.jwp.model.http;

public enum HTTPHeaders {
    PROTOCOL("HTTP/1.1 "),
    LOCATION("Location: "),
    CONTENT_TYPE(ContentType.NAME),
    CONTENT_LENGTH("Content-Length: ");

    private final String value;

    HTTPHeaders(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
