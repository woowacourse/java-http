package nextstep.jwp.framework.infrastructure.http.header;

public enum HttpHeaders {
    CONTENT_LENGTH("Content-Length"),
    CONTENT_TYPE("Content-Type"),
    LOCATION("Location");

    private final String signature;

    HttpHeaders(String signature) {
        this.signature = signature;
    }

    public String getSignature() {
        return signature;
    }
}
