package nextstep.jwp.model.http;

public enum HTTPProtocol {
    OK("200","OK"),
    REDIRECT("302","Found");

    private final String httpStatus;
    private final String message;

    HTTPProtocol(String httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public String getProtocol() {
        return httpStatus + " " + message;
    }
}
