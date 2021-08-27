package nextstep.jwp.framework.http;

import java.util.Arrays;
import java.util.List;

import nextstep.jwp.framework.util.MultiValueMap;

public class HttpRequestBuilder {
    private HttpMethod method;
    private String uri;
    private String version;
    private MultiValueMap<String, String> headers;
    private String requestBody;

    public HttpRequestBuilder() {
        this.headers = new MultiValueMap<>();
    }

    public HttpRequestBuilder httpMethod(HttpMethod method) {
        this.method = method;
        return this;
    }

    public HttpRequestBuilder uri(String uri) {
        this.uri = uri;
        return this;
    }

    public HttpRequestBuilder version(String version) {
        this.version = version;
        return this;
    }

    public HttpRequest build() {
        return new HttpRequest(method, uri, version, headers, requestBody);
    }

    public HttpRequestBuilder header(String name, String... values) {
        return header(name, Arrays.asList(values));
    }

    public HttpRequestBuilder header(String name, List<String> values) {
        this.headers.addAll(name, values);
        return this;
    }
}
