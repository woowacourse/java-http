package nextstep.jwp.vo;

public enum ResponseStatus {

    OK(200, "OK"),
    FOUND(302, "Found");

    private final int code;
    private final String status;

    ResponseStatus(int code, String status) {
        this.code = code;
        this.status = status;
    }

    public int getCode() {
        return code;
    }

    public String getStatus() {
        return status;
    }
}
