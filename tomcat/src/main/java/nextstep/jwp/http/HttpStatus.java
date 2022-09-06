package nextstep.jwp.http;

public enum HttpStatus {

    OK(200, "OK"),
    FOUND(302, "Found"),
    ;

    private final int value;
    private final String reasonResponse;

    HttpStatus(final int value, final String reasonResponse) {
        this.value = value;
        this.reasonResponse = reasonResponse;
    }

    public String httpResponseHeaderStatus() {
        return String.format("%d %s", value, reasonResponse);
    }
}
