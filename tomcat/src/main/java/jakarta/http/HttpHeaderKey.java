package jakarta.http;

public enum HttpHeaderKey {

    CONTENT_LENGTH("Content-Length"),
    CONTENT_TYPE("Content-Type"),
    LOCATION("Location"),
    COOKIE("Cookie"),
    SET_COOKIE("Set-Cookie"),
    ;

    private final String name;

    HttpHeaderKey(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
