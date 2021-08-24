package nextstep.jwp.http.request;

import java.util.Optional;
import nextstep.jwp.http.Body;
import nextstep.jwp.http.Headers;
import nextstep.jwp.http.request.request_line.RequestLine;

public class HttpRequest {

    private final RequestLine requestLine;
    private final Headers headers;
    private final Body body;

    public HttpRequest(String httpRequest) {
        this.requestLine = new RequestLine(httpRequest);
        this.headers = new Headers(httpRequest);
        this.body = new Body(httpRequest);
    }

    public Optional<String> getHeader(String header) {
        return headers.getHeader(header);
    }

    public String getHttpMethod() {
        return requestLine.getHttpMethod();
    }

    public String getResourcePath() {
        return requestLine.getPath();
    }
}
