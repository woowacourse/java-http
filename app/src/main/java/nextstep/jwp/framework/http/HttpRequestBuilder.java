package nextstep.jwp.framework.http;

import java.util.Arrays;
import java.util.List;

public class HttpRequestBuilder {
    private RequestLine requestLine;
    private final HttpHeaders headers;
    private final StringBuilder requestBody;

    public HttpRequestBuilder() {
        this.headers = new HttpHeaders();
        this.requestBody = new StringBuilder();
    }

    public HttpRequestBuilder requestLine(HttpMethod method, String uri, String version) {
        return requestLine(new RequestLine(method, uri, version));
    }

    public HttpRequestBuilder requestLine(RequestLine requestLine) {
        this.requestLine = requestLine;
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
        return new HttpRequest(requestLine, headers, requestBody);
    }
}
