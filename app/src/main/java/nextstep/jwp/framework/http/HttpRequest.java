package nextstep.jwp.framework.http;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import nextstep.jwp.framework.http.formatter.LineFormatter;
import nextstep.jwp.framework.http.formatter.RequestLineFormatter;

public class HttpRequest implements HttpMessage {

    private static final String EMPTY = "";

    private final RequestLine requestLine;
    private final HttpHeaders httpHeaders;
    private final String requestBody;

    public HttpRequest(RequestLine requestLine, HttpHeaders httpHeaders, String requestBody) {
        this.requestLine = requestLine;
        this.httpHeaders = httpHeaders;
        this.requestBody = requestBody;
    }

    public HttpMethod getMethod() {
        return requestLine.getMethod();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public String getVersion() {
        return requestLine.getVersion();
    }

    public Map<String, String> getQueries() {
        return requestLine.getQueries();
    }

    public String getValueFromQuery(String key) {
        return getQueries().get(key);
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public boolean isSamePath(String uri) {
        return requestLine.isSamePath(uri);
    }

    public String readAfterExceptBody() {
        final HttpRequest httpRequest = new HttpRequest.Builder().copy(this)
                                                                 .overwrite(EMPTY)
                                                                 .build();

        LineFormatter lineFormatter = new RequestLineFormatter(httpRequest);
        StringBuilder stringBuilder = new StringBuilder();
        while (lineFormatter.canRead()) {
            stringBuilder.append(lineFormatter.transform());
            lineFormatter = lineFormatter.convertNextFormatter();
        }
        return stringBuilder.toString();
    }

    @Override
    public HttpHeaders getHttpHeaders() {
        return httpHeaders;
    }

    @Override
    public String getBody() {
        return requestBody;
    }

    public static class Builder {
        private RequestLine requestLine;
        private HttpHeaders httpHeaders;
        private StringBuilder requestBody;

        public Builder() {
            this.httpHeaders = new HttpHeaders();
            this.requestBody = new StringBuilder();
        }

        public Builder requestLine(HttpMethod method, String uri, HttpVersion version) {
            return requestLine(new RequestLine(method, uri, version));
        }

        public Builder requestLine(HttpMethod method, URI uri, HttpVersion version) {
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
            this.httpHeaders.addHeader(name, values);
            return this;
        }

        public Builder httpHeaders(HttpHeaders httpHeaders) {
            this.httpHeaders = httpHeaders;
            return this;
        }

        public Builder body(String line) {
            this.requestBody.append(line)
                            .append("\r\n");

            return this;
        }

        public Builder overwrite(String requestBody) {
            this.requestBody = new StringBuilder().append(requestBody);
            return this;
        }

        public Builder copy(HttpRequest httpRequest) {
            requestLine(httpRequest.requestLine);
            httpHeaders(httpRequest.httpHeaders);
            overwrite(httpRequest.requestBody);
            return this;
        }

        public HttpRequest build() {
            return new HttpRequest(requestLine, httpHeaders, requestBody.toString());
        }
    }
}
