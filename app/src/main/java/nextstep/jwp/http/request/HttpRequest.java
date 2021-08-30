package nextstep.jwp.http.request;

import java.io.BufferedReader;
import java.io.IOException;
import nextstep.jwp.http.common.HttpHeaders;
import nextstep.jwp.http.common.HttpMethod;
import nextstep.jwp.http.infra.RequestBodyBuilder;
import nextstep.jwp.http.infra.RequestHeaderBuilder;

public class HttpRequest {

    private HttpMethod method;
    private URI uri;
    private HttpHeaders httpHeaders;
    private RequestBody requestBody;

    private HttpRequest(Builder builder) {
        method = builder.method;
        uri = builder.uri;
        httpHeaders = builder.httpHeaders;
        requestBody = builder.requestBody;
    }

    public static class Builder {

        private HttpMethod method;
        private URI uri;
        private RequestHeaderBuilder requestHeaderBuilder;
        private HttpHeaders httpHeaders;
        private RequestBodyBuilder requestBodyBuilder;
        private RequestBody requestBody;

        public Builder(BufferedReader bufferedReader) {
            requestHeaderBuilder = new RequestHeaderBuilder();
            requestBodyBuilder = new RequestBodyBuilder(bufferedReader);
        }

        public Builder method(String method) {
            this.method = HttpMethod.valueOf(method);
            return this;
        }

        public Builder uri(String uri) {
            this.uri = URI.of(uri);
            return this;
        }

        public Builder requestHeaders(String[] splitRequestHeaders) {
            this.httpHeaders = requestHeaderBuilder.addHeader(splitRequestHeaders).build();
            return this;
        }

        public Builder requestBody() throws IOException {
            this.requestBody = this.requestBodyBuilder.addBody(httpHeaders).build();
            return this;
        }

        public HttpRequest build() {
            return new HttpRequest(this);
        }
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return uri.getResourceUri();
    }

    public HttpHeaders getRequestHeader() {
        return httpHeaders;
    }

    public RequestBody getRequestBody() {
        return requestBody;
    }

    public String getParameter(String key) {
        return requestBody.getParameter(key);
    }
}
