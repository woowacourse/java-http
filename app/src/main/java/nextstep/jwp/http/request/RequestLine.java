package nextstep.jwp.http.request;

public class RequestLine {

    private final String method;
    private final String path;
    private final String protocolVersion;

    public RequestLine(String method, String path, String protocolVersion) {
        this.method = method;
        this.path = path;
        this.protocolVersion = protocolVersion;
    }

    public static RequestLine of(String line) {
        String[] params = line.split(" ");
        return new RequestLine(params[0], params[1], params[2]);
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }
}
