package nextstep.jwp.model;

import org.apache.coyote.http11.model.Parameters;

public class RequestLine {

    private String method;
    private String uri;
    private String version;

    private RequestLine() {
    }

    public RequestLine(final String requestLine) {
        final String[] values = requestLine.split(" ");
        this.method = values[0];
        this.uri = values[1];
        this.version = values[2];
    }

    public String getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    public String getPath() {
        return Path.fromUri(uri)
                .get();
    }

    public String getVersion() {
        return version;
    }

    public Parameters getParameters() {
        return Parameters.fromUri(uri);
    }
}
