package nextstep.jwp.http.request;

import java.util.Optional;
import nextstep.jwp.http.Body;
import nextstep.jwp.http.Headers;
import nextstep.jwp.http.HttpVersion;
import nextstep.jwp.http.request.request_line.HttpMethod;
import nextstep.jwp.http.request.request_line.HttpPath;
import nextstep.jwp.http.request.request_line.RequestLine;

public class HttpRequest {

    private final RequestLine requestLine;
    private final Headers headers;
    private final Body body;

    public HttpRequest(String httpRequest) {
        this.requestLine = new RequestLine(httpRequest);
        this.headers = new Headers(httpRequest);
        this.body = Body.fromHttpRequest(httpRequest);
    }

    public String getRequestLine() {
        return requestLine.toString();
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
