package nextstep.jwp.http.header.request;

import java.util.Optional;
import nextstep.jwp.http.header.element.Body;
import nextstep.jwp.http.header.element.Headers;
import nextstep.jwp.http.header.element.HttpVersion;
import nextstep.jwp.http.header.request.request_line.HttpMethod;
import nextstep.jwp.http.header.request.request_line.HttpPath;
import nextstep.jwp.http.header.request.request_line.RequestLine;

public class HttpRequest {

    private final RequestLine requestLine;
    private final Headers headers;
    private final Body body;

    public HttpRequest(RequestLine requestLine, Headers headers, Body body) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.body = body;
    }

    public Optional<String> getHeader(String header) {
        return headers.getHeader(header);
    }

    public HttpMethod getHttpMethod() {
        return requestLine.getHttpMethod();
    }

    public HttpPath getPath() {
        return requestLine.getPath();
    }

    public HttpVersion getVersion() {
        return requestLine.getVersion();
    }

    public Body getBody() {
        return body;
    }
}
