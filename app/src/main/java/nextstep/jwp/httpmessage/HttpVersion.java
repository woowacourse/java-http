package nextstep.jwp.httpmessage;

public enum HttpVersion {
    HTTP1_1("HTTP/1.1"),
    HTTP_2("HTTP/2");

    private final String value;

    HttpVersion(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
