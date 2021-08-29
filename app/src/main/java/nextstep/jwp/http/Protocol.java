package nextstep.jwp.http;

public enum Protocol {
    LINE_SEPARATOR("\r\n");

    private final String value;

    Protocol(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}