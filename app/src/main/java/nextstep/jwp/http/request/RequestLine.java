package nextstep.jwp.http.request;

import java.util.Objects;

public class RequestLine {

    private final HttpMethod method;
    private final RequestUriPath uriPath;
    private final String protocolVersion;

    private RequestLine(HttpMethod method, RequestUriPath path, String protocolVersion) {
        this.method = method;
        this.uriPath = path;
        this.protocolVersion = protocolVersion;
    }

    public static RequestLine of(String line) {
        String[] params = line.split(" ");
        return new RequestLine(HttpMethod.parse(params[0]), RequestUriPath.of(params[1]), params[2]);
    }

    public HttpMethod method() {
        return method;
    }

    public SourcePath sourcePath() {
        return uriPath.getSourcePath();
    }

    public QueryParams queryParams(){
        return uriPath.getQueryParams();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RequestLine that = (RequestLine) o;
        return method == that.method && Objects.equals(uriPath, that.uriPath) && Objects
                .equals(protocolVersion, that.protocolVersion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(method, uriPath, protocolVersion);
    }
}
