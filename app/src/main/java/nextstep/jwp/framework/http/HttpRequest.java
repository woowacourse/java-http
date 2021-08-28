package nextstep.jwp.framework.http;

import java.util.Arrays;
import java.util.List;

public class HttpRequest {
    private final RequestLine requestLine;
    private final HttpHeaders headers;
    private final String requestBody;

    public HttpRequest(RequestLine requestLine, HttpHeaders headers, String requestBody) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.requestBody = requestBody;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public HttpMethod getMethod() {
        return requestLine.getMethod();
    }

    public String getUri() {
        return requestLine.getUri();
    }

    public String getVersion() {
        return requestLine.getVersion();
    }

    public boolean isSameUri(String uri) {
        return requestLine.isSameUri(uri);
    }

    public static class Builder {
        private RequestLine requestLine;
        private final HttpHeaders headers;
        private final StringBuilder requestBody;

        public Builder() {
            this.headers = new HttpHeaders();
            this.requestBody = new StringBuilder();
        }

        public Builder requestLine(HttpMethod method, String uri, HttpVersion version) {
            return requestLine(new RequestLine(method, uri, version));
        }

        public Builder requestLine(RequestLine requestLine) {
            this.requestLine = requestLine;
            return this;
        }

        public Builder header(String name, String... values) {
            return header(name, Arrays.asList(values));
        }

        public Builder header(String name, List<String> values) {
            this.headers.addHeader(name, values);
            return this;
        }

        public Builder body(String line) {
            this.requestBody.append(line)
                            .append("\r\n");

            return this;
        }

        public HttpRequest build() {
            return new HttpRequest(requestLine, headers, requestBody.toString());
        }
    }
}
