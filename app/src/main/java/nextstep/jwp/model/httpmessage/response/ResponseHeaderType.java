package nextstep.jwp.model.httpmessage.response;

public enum ResponseHeaderType {
    COOKIE("Set-Cookie"),
    LOCATION("Location");

    private final String value;

    ResponseHeaderType(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
