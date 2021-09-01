package nextstep.jwp.framework.infrastructure.http.header;

public enum HttpHeaders {
    CONTENT_LENGTH("Content-Length");

    private final String signature;

    HttpHeaders(String signature) {
        this.signature = signature;
    }

    public String getSignature() {
        return signature;
    }
}
