package nextstep.jwp.model.web.request;

public class RequestLine {

    private final String method;
    private final String path;
    private final String versionOfProtocol;

    public RequestLine(String[] requestLines) {
        this(requestLines[0], requestLines[1], requestLines[2]);
    }

    public RequestLine(String method, String path, String versionOfProtocol) {
        this.method = method;
        this.path = path;
        this.versionOfProtocol = versionOfProtocol;
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getVersionOfProtocol() {
        return versionOfProtocol;
    }
}
