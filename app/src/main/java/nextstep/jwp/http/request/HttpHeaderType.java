package nextstep.jwp.http.request;

public enum HttpHeaderType {
    START_LINE("START_LINE"),
    CONTENT_LENGTH("Content-Length"),
    COOKIE("Cookie");

    private final String headerType;

    HttpHeaderType(String headerType) {
        this.headerType = headerType;
    }

    public String value() {
        return headerType;
    }
}
