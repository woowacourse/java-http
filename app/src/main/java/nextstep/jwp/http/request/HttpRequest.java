package nextstep.jwp.http.request;

import java.io.BufferedReader;
import java.io.IOException;
import nextstep.jwp.http.common.HttpHeaders;
import nextstep.jwp.http.common.HttpMethod;
import nextstep.jwp.http.util.RequestBodyBuilder;
import nextstep.jwp.http.util.RequestHeaderBuilder;

public class HttpRequest {

    private RequestLine requestLine;
    private HttpHeaders httpHeaders;
    private RequestBody requestBody;

    private HttpRequest(Builder builder) {
        requestLine = builder.requestLine;
        httpHeaders = builder.httpHeaders;
        requestBody = builder.requestBody;
    }

    public HttpMethod getMethod() {
        return requestLine.getHttpMethod();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public String getQueryParameter(String key) {
        return requestLine.getQueryParameter(key);
    }

    public String getBodyParameter(String key) {
        return requestBody.getParameter(key);
    }

    public static class Builder {

        private RequestLine requestLine;
        private RequestHeaderBuilder requestHeaderBuilder;
        private HttpHeaders httpHeaders;
        private RequestBodyBuilder requestBodyBuilder;
        private RequestBody requestBody;

        public Builder(BufferedReader bufferedReader) {
            requestHeaderBuilder = new RequestHeaderBuilder();
            requestBodyBuilder = new RequestBodyBuilder(bufferedReader);
        }

        public Builder requestLine(String requestLine) {
            this.requestLine = RequestLine.of(requestLine);
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
}
