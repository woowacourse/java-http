package kokodak;

public enum Constants {

    CRLF("\r\n"),
    BLANK(" "),
    COLON(":"),
    ;

    private String value;

    Constants(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
