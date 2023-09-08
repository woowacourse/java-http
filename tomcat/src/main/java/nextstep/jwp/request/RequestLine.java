package nextstep.jwp.request;

import java.util.Map;
import nextstep.jwp.common.HttpMethod;
import nextstep.jwp.common.HttpVersion;

public class RequestLine {

    private static final int METHOD_INDEX = 0;
    private static final int URI_INDEX = 1;
    private static final int VERSION_INDEX = 2;

    private final HttpMethod httpMethod;
    private final RequestUri requestUri;
    private final HttpVersion httpVersion;

    private RequestLine(HttpMethod httpMethod, RequestUri requestURI, HttpVersion httpVersion) {
        this.httpMethod = httpMethod;
        this.requestUri = requestURI;
        this.httpVersion = httpVersion;
    }

    public static RequestLine from(final String requestLine) {
        final String[] params = requestLine.split(" ");
        final HttpMethod httpMethod = HttpMethod.from(params[METHOD_INDEX]);
        final RequestUri uri = RequestUri.from(params[1].substring(URI_INDEX));
        final HttpVersion httpVersion = HttpVersion.from(params[VERSION_INDEX]);
        return new RequestLine(httpMethod, uri, httpVersion);
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getRequestUri() {
        return requestUri.getUri();
    }

    public Map<String, String> getQueryParams() {
        return requestUri.getQueryParams();
    }

    public HttpVersion getHttpVersion() {
        return httpVersion;
    }
}
