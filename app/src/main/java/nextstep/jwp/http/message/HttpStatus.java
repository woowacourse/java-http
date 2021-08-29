package nextstep.jwp.http.message;

public enum HttpStatus {
    OK(200, "OK"),
    NOT_FOUND(404, "Not Found");

    private final int status;
    private final String description;

    HttpStatus(int status, String description) {
        this.status = status;
        this.description = description;
    }

    public int getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }
}
