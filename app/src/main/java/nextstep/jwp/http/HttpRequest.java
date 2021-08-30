package nextstep.jwp.http;

import java.util.Map;

public class HttpRequest {

    private final String method;
    private final String uri;
    private final Map<String, String> headers;
    private final String body;

    public HttpRequest(String method, String uri,
            Map<String, String> headers, String body) {
        this.method = method;
        this.uri = uri;
        this.headers = headers;
        this.body = body;
    }

    public String method() {
        return method;
    }

    public String uri() {
        return uri;
    }

    public String body() {
        return body;
    }
}
