package nextstep.jwp.httpserver.domain;

public enum StatusCode {
    OK(200, "OK"),
    CREATED(201, "Created");

    private final int code;
    private final String statusText;

    StatusCode(int code, String statusText) {
        this.code = code;
        this.statusText = statusText;
    }

    public int getCode() {
        return code;
    }

    public String getStatusText() {
        return statusText;
    }
}
