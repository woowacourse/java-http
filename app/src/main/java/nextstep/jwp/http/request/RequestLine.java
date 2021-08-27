package nextstep.jwp.http.request;

/**
 *  GET /index.html HTTP/1.1
 *  GET /login?something1=123&something2=123  HTTP/1.1
 */

public class RequestLine {

    private final String method;
    private final RequestUriPath uriPath;
    private final String protocolVersion;

    public RequestLine(String method, RequestUriPath path, String protocolVersion) {
        this.method = method;
        this.uriPath = path;
        this.protocolVersion = protocolVersion;
    }

    public static RequestLine of(String line) {
        String[] params = line.split(" ");
        return new RequestLine(params[0], RequestUriPath.of(params[1]), params[2]);
    }

    public String getMethod() {
        return method;
    }

    public RequestUriPath getUriPath() {
        return uriPath;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }
}
