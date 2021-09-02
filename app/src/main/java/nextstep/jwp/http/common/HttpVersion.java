package nextstep.jwp.http.common;

public enum HttpVersion {
    HTTP_1_1("HTTP/1.1");

    private String value;

    HttpVersion(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
