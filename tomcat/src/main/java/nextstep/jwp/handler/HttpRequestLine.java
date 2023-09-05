package nextstep.jwp.handler;

import org.apache.coyote.http11.HttpMethod;

public class HttpRequestLine {

    private final HttpMethod method;
    private final String path;
    private final String version;

    public HttpRequestLine(String method, String path, String version) {
        this.method = HttpMethod.from(method);
        this.path = path;
        this.version = version;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getVersion() {
        return version;
    }
}
