package nextstep.jwp.http;

public enum HttpStatus {
    OK(200, "Ok"),
    FOUND(302, "Found");

    private final int code;
    private final String status;

    HttpStatus(int code, String status) {
        this.code = code;
        this.status = status;
    }

    public String asString() {
        return String.format("%d %s", code, status);
    }
}
