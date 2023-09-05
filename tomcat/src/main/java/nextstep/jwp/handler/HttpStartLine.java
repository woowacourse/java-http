package nextstep.jwp.handler;

import org.apache.coyote.http11.HttpMethod;

public class HttpStartLine {

    private final HttpMethod method;
    private final String target;
    private final String version;

    public HttpStartLine(String method, String target, String version) {
        this.method = HttpMethod.from(method);
        this.target = target;
        this.version = version;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getTarget() {
        return target;
    }

    public String getVersion() {
        return version;
    }
}
