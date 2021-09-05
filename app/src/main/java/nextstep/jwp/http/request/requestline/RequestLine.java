package nextstep.jwp.http.request.requestline;

import nextstep.jwp.http.common.HttpVersion;

public class RequestLine {

    private static final int METHOD_INDEX = 0;
    private static final int URI_INDEX = 1;
    private static final int VERSION_INDEX = 2;

    private final HttpMethod method;
    private final RequestPath requestPath;
    private final HttpVersion httpVersion;

    private RequestLine(HttpMethod method, RequestPath requestPath,
        HttpVersion httpVersion) {
        this.method = method;
        this.requestPath = requestPath;
        this.httpVersion = httpVersion;
    }

    public static RequestLine parse(String requestLine) {
        String[] slicedLine = requestLine.split(" ");

        HttpMethod method = HttpMethod.matchOf(slicedLine[METHOD_INDEX]);
        RequestPath requestPath = new RequestPath(slicedLine[URI_INDEX]);
        HttpVersion httpVersion = HttpVersion.matchOf(slicedLine[VERSION_INDEX]);
        return new RequestLine(method, requestPath, httpVersion);
    }

    public RequestPath getRequestURI() {
        return requestPath;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public HttpVersion getHttpVersion() {
        return httpVersion;
    }
}
