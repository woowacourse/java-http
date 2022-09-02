package nextstep.jwp.http.request;

import java.util.Map;
import nextstep.jwp.exception.InvalidRequestLineException;

public class RequestLine {

    private static final String BLANK = " ";
    private static final int REQUEST_LINE_LENGTH = 3;
    private static final int METHOD_INDEX = 0;
    private static final int URI_INDEX = 1;
    private static final int VERSION_INDEX = 2;

    private final RequestMethod requestMethod;
    private final RequestUri requestUri;

    public RequestLine(final RequestMethod requestMethod,
                       final RequestUri requestUri) {
        this.requestMethod = requestMethod;
        this.requestUri = requestUri;
    }

    public static RequestLine from(final String requestLine) {
        String[] requestValues = splitRequestLine(requestLine);

        RequestMethod requestMethod = RequestMethod.from(requestValues[METHOD_INDEX]);
        RequestUri requestUri = RequestUri.from(requestValues[URI_INDEX]);
        // TODO: HttpVersion 부족한 부분이 있어 임시 주석
        // HttpVersion httpVersion = HttpVersion.from(requestValues[VERSION_INDEX]);

        return new RequestLine(requestMethod, requestUri);
    }

    private static String[] splitRequestLine(final String requestLine) {
        String[] requestValue = requestLine.split(BLANK);
        if (requestValue.length != REQUEST_LINE_LENGTH) {
            throw new InvalidRequestLineException();
        }

        return requestValue;
    }

    public RequestMethod getRequestMethod() {
        return requestMethod;
    }

    public String getRequestUri() {
        return requestUri.getValue();
    }

    public Map<String, String> getQueryParameters() {
        return requestUri.getQueryParameters();
    }

    public String getUriParameter(String parameter) {
        return requestUri.getQueryParameter(parameter);
    }
}
