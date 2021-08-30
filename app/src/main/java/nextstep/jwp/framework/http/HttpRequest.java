package nextstep.jwp.framework.http;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Objects;

import nextstep.jwp.framework.http.formatter.HttpFormatter;
import nextstep.jwp.framework.http.formatter.RequestLineFormatter;
import nextstep.jwp.framework.http.parser.HttpRequestParser;

public class HttpRequest implements HttpMessage {

    private static final String EMPTY = "";

    private final RequestLine requestLine;
    private final HttpHeaders httpHeaders;
    private final String requestBody;

    public HttpRequest(RequestLine requestLine, HttpHeaders httpHeaders, String requestBody) {
        this.requestLine = Objects.requireNonNull(requestLine);
        this.httpHeaders = Objects.requireNonNull(httpHeaders);
        this.requestBody = Objects.requireNonNullElse(requestBody, EMPTY);
    }

    public static HttpRequest from(InputStream inputStream) throws IOException {
        return new HttpRequestParser(inputStream).parseRequest();
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

        HttpFormatter httpFormatter = new RequestLineFormatter(httpRequest.requestLine);
        StringBuilder stringBuilder = new StringBuilder();
        while (httpFormatter.canRead()) {
            stringBuilder.append(httpFormatter.transform());
            httpFormatter = httpFormatter.convertNextFormatter(httpRequest);
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

        public Builder header(String name, String value) {
            this.httpHeaders.addHeader(name, value);
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
            httpHeaders(new HttpHeaders(httpRequest.httpHeaders));
            overwrite(httpRequest.requestBody);
            return this;
        }

        public HttpRequest build() {
            return new HttpRequest(requestLine, httpHeaders, requestBody.toString());
        }

        public boolean hasHeader(String headerName) {
            return this.httpHeaders.contains(headerName);
        }

        public String getHeaderValue(String headerName) {
            return this.httpHeaders.get(headerName);
        }
    }
}
