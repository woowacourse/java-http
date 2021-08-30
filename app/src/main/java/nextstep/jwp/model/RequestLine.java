package nextstep.jwp.model;

public class RequestLine {

    private final String method;
    private final Uri uri;
    private final String version;

    public RequestLine(String method, Uri uri, String version) {
        this.method = method;
        this.uri = uri;
        this.version = version;
    }

    public String getMethod() {
        return method;
    }

    public Uri getUri() {
        return uri;
    }

    public String getVersion() {
        return version;
    }
}
