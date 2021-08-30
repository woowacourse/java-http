package nextstep.jwp.model.httpmessage.response;

public enum ResponseHeaderType {

    LOCATION("Location");

    private final String value;

    ResponseHeaderType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
