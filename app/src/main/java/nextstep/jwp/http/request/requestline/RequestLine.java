package nextstep.jwp.http.request.requestline;

import nextstep.jwp.http.common.HttpVersion;

public class RequestLine {

    private static final int METHOD_INDEX = 0;
    private static final int URI_INDEX = 1;
    private static final int VERSION_INDEX = 2;

    private final HttpMethod method;
    private final RequestURI requestURI;
    private final HttpVersion httpVersion;

    private RequestLine(HttpMethod method, RequestURI requestURI,
        HttpVersion httpVersion) {
        this.method = method;
        this.requestURI = requestURI;
        this.httpVersion = httpVersion;
    }

    public static RequestLine parse(String requestLine) {
        String[] slicedLine = requestLine.split(" ");

        HttpMethod method = HttpMethod.matchOf(slicedLine[METHOD_INDEX]);
        RequestURI requestURI = new RequestURI(slicedLine[URI_INDEX]);
        HttpVersion httpVersion = HttpVersion.matchOf(slicedLine[VERSION_INDEX]);

        return new RequestLine(method, requestURI, httpVersion);
    }

    public HttpMethod getMethod() {
        return method;
    }
}
