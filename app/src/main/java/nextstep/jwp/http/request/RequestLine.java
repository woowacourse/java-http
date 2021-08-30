package nextstep.jwp.http.request;

import nextstep.jwp.exception.InvalidRequestLineException;
import nextstep.jwp.http.common.HttpVersion;

public class RequestLine {

    private static final int METHOD_INDEX = 0;
    private static final int URI_INDEX = 1;
    private static final int VERSION_INDEX = 2;
    private static final int EXPECT_REQUEST_LINE_LENGTH = 3;
    private static final String SPACE = " ";

    private final Method method;
    private final RequestUri requestUri;
    private final HttpVersion httpVersion;

    private RequestLine(Method method, RequestUri requestUri, HttpVersion httpVersion) {
        this.method = method;
        this.requestUri = requestUri;
        this.httpVersion = httpVersion;
    }

    public static RequestLine parse(String requestLine) {
        String[] splitedLine = splitRequestLine(requestLine);

        Method method = Method.matchOf(splitedLine[METHOD_INDEX]);
        RequestUri requestURI = RequestUri.parse(splitedLine[URI_INDEX]);
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

    public String getUriParameter(String parameter) {
        return requestUri.getQueryParameter(parameter);
    }

    public String getUri() {
        return requestUri.getValue();
    }

    public boolean isSameMethod(Method method) {
        return method.equals(this.method);
    }

    public boolean hasQueryParam() {
        return requestUri.hasQueryParam();
    }

    @Override
    public String toString() {
        return String.join(SPACE, method.name(), requestUri.toString(), httpVersion.getValue());
    }
}
