package kokodak;

public enum HttpVersion {

    HTTP11("HTTP/1.1"),
    ;

    private String value;

    HttpVersion(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
