package nextstep.jwp.http.common;

public class HttpCookie {

    private final String key;
    private final String value;

    public HttpCookie(final String key, final String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
