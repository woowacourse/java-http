package nextstep.jwp.model.http;

public enum HttpProtocol {
    OK("200", "OK"),
    REDIRECT("302", "Found");

    public static final String NAME = "HTTP/1.1";
    private final String httpStatus;
    private final String message;

    HttpProtocol(String httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public String getProtocol() {
        return NAME + " " + httpStatus + " " + message;
    }
}
