package nextstep.jwp.model.httpMessage.response;

public enum ResponseHeaderType {

    LOCATION("Location");

    private final String value;

    ResponseHeaderType(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

}
