package nextstep.jwp.http.request;

import java.util.Objects;

/**
 *  GET /index.html HTTP/1.1
 *  GET /login?something1=123&something2=123 HTTP/1.1
 */

public class RequestLine {

    private final String method;
    private final RequestUriPath uriPath;
    private final String protocolVersion;

    private RequestLine(String method, RequestUriPath path, String protocolVersion) {
        this.method = method;
        this.uriPath = path;
        this.protocolVersion = protocolVersion;
    }

    public static RequestLine of(String line) {
        String[] params = line.split(" ");
        return new RequestLine(params[0], RequestUriPath.of(params[1]), params[2]);
    }

    public String method() {
        return method;
    }

    public SourcePath sourcePath(){
        return uriPath.getSourcePath();
    }

    public QueryParams queryParams(){
        return uriPath.getQueryParams();
    }

    public boolean isFrom(String method, String path){
        return this.method.equalsIgnoreCase(method) && this.uriPath.isPath(path);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RequestLine that = (RequestLine) o;
        return Objects.equals(method, that.method) && Objects.equals(uriPath, that.uriPath) && Objects.equals(protocolVersion, that.protocolVersion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(method, uriPath, protocolVersion);
    }
}
