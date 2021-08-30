package nextstep.jwp.model;

public class RequestLine {

    private final String method;
    private final String uri;
    private final String version;

    public RequestLine(String method, String uri, String version) {
        this.method = method;
        this.uri = uri;
        this.version = version;
    }

    public String getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    public String getVersion() {
        return version;
    }
}
