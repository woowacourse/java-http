package nextstep.jwp.http;

public enum HttpVersion {
    HTTP1_1("HTTP/1.1");

    private final String version;

    HttpVersion(String version) {
        this.version = version;
    }

    public String asString() {
        return version;
    }
}
