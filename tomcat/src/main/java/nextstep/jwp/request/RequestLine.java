package nextstep.jwp.request;

import nextstep.jwp.common.HttpMethod;
import nextstep.jwp.common.HttpVersion;

public class RequestLine {

    private final HttpMethod httpMethod;
    private final RequestUri requestURI;
    private final HttpVersion httpVersion;

    private RequestLine(HttpMethod httpMethod, RequestUri requestURI, HttpVersion httpVersion) {
        this.httpMethod = httpMethod;
        this.requestURI = requestURI;
        this.httpVersion = httpVersion;
    }

    public static RequestLine of(final String line) {
        final String[] inputs = line.split(" ");
        final HttpMethod httpMethod = HttpMethod.of(inputs[0]);
        final RequestUri requestURI = RequestUri.of(inputs[1]);
        final HttpVersion httpVersion = HttpVersion.of(inputs[2]);
        return new RequestLine(httpMethod, requestURI, httpVersion);
    }

    public String getRequestUri() {
        return requestURI.getUri();
    }
}
