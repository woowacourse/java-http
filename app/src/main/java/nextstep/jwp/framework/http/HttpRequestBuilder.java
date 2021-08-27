package nextstep.jwp.framework.http;

import java.util.Arrays;
import java.util.List;

public class HttpRequestBuilder {
    private HttpMethod method;
    private String uri;
    private String version;
    private final HttpHeaders headers;
    private final StringBuilder requestBody;

    public HttpRequestBuilder() {
        this.headers = new HttpHeaders();
        this.requestBody = new StringBuilder();
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

    public HttpRequestBuilder header(String name, String... values) {
        return header(name, Arrays.asList(values));
    }

    public HttpRequestBuilder header(String name, List<String> values) {
        this.headers.addHeader(name, values);
        return this;
    }

    public HttpRequestBuilder body(String line) {
        this.requestBody.append(line)
                        .append("\r")
                        .append(System.lineSeparator());

        return this;
    }

    public HttpRequest build() {
        return new HttpRequest(method, uri, version, headers, requestBody);
    }
}
