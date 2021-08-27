package nextstep.jwp.http.request.requestline;

import nextstep.jwp.exception.InvalidRequestLineException;

public class RequestLine {

    private static final int METHOD_INDEX = 0;
    private static final int URI_INDEX = 1;
    private static final int VERSION_INDEX = 2;
    private static final int EXPECT_REQUEST_LINE_LENGTH = 3;

    private final Method method;
    private final RequestURI requestURI;
    private final HttpVersion httpVersion;

    private RequestLine(Method method, RequestURI requestURI, HttpVersion httpVersion) {
        this.method = method;
        this.requestURI = requestURI;
        this.httpVersion = httpVersion;
    }

    public static RequestLine parse(String requestLine) {
        String[] splitedLine = splitRequestLine(requestLine);

        Method method = Method.matchOf(splitedLine[METHOD_INDEX]);
        RequestURI requestURI = new RequestURI(splitedLine[URI_INDEX]);
        HttpVersion httpVersion = HttpVersion.matchOf(splitedLine[VERSION_INDEX]);

        return new RequestLine(method, requestURI, httpVersion);
    }

    private static String[] splitRequestLine(String requestLine) {
        String[] splitedLine = requestLine.split(" ");

        if (splitedLine.length != EXPECT_REQUEST_LINE_LENGTH) {
            throw new InvalidRequestLineException();
        }

        return splitedLine;
    }
}
