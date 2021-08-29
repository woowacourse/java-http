package nextstep.jwp.http;

public class Protocol {

    String value;

    public Protocol(String value) {
        validateProtocol(value);
        this.value = value;
    }

    private void validateProtocol(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException();
        }
    }

    public String getValue() {
        return value;
    }
}
