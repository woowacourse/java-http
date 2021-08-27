package nextstep.jwp.framework.http;

import nextstep.jwp.framework.util.MultiValueMap;

public class HttpRequest {
    private final HttpMethod method;
    private final String uri;
    private final String version;
    private final MultiValueMap<String, String> headers;
    private final String requestBody;

    public HttpRequest(HttpMethod method, String uri, String version, MultiValueMap<String, String> headers, String requestBody) {
        this.method = method;
        this.uri = uri;
        this.version = version;
        this.headers = headers;
        this.requestBody = requestBody;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    public String getVersion() {
        return version;
    }
}
