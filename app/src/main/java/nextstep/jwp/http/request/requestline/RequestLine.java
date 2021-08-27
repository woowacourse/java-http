package nextstep.jwp.http.request.requestline;

public class RequestLine {

    private static final int METHOD_INDEX = 0;
    private static final int URI_INDEX = 1;
    private static final int VERSION_INDEX = 2;

    private final Method method;
    private final RequestURI requestURI;
    private final HttpVersion httpVersion;

    private RequestLine(Method method, RequestURI requestURI, HttpVersion httpVersion) {
        this.method = method;
        this.requestURI = requestURI;
        this.httpVersion = httpVersion;
    }

    public static RequestLine parse(String requestLine) {
        String[] splitedRequestLine = requestLine.split(" ");

        Method method = Method.matchOf(splitedRequestLine[METHOD_INDEX]);
        RequestURI requestURI = new RequestURI(splitedRequestLine[URI_INDEX]);
        HttpVersion httpVersion = HttpVersion.matchOf(splitedRequestLine[VERSION_INDEX]);

        return new RequestLine(method, requestURI, httpVersion);
    }
}
