package nextstep.jwp.model.response;

public enum StatusType {

    OK(200, "OK"),
    FOUND(302, "FOUND");

    private final int code;
    private final String text;

    StatusType(int code, String text) {
        this.code = code;
        this.text = text;
    }

    public int code() {
        return code;
    }

    public String text() {
        return text;
    }
}
