package nextstep.jwp.http.request;

import java.util.Map;
import nextstep.jwp.exception.InvalidRequestExtensionException;
import nextstep.jwp.exception.InvalidRequestLineException;

public class RequestLine {

    private static final String BLANK = " ";
    private static final int REQUEST_LINE_LENGTH = 3;
    private static final String REQUEST_URI_EXTENSION_DELIMITER = "\\.";
    private static final int EXPECT_URI_EXTENSION_LENGTH = 2;
    private static final int EXTENSION_INDEX = 1;
    private static final int REQUEST_METHOD_INDEX = 0;
    private static final int REQUEST_URI_INDEX = 1;
    private static final int REQUEST_HTTP_VERSION_INDEX = 2;

    private final RequestMethod requestMethod;
    private final RequestUri requestUri;
    private final String httpVersion;

    public RequestLine(final RequestMethod requestMethod,
                       final RequestUri requestUri,
                       final String httpVersion) {
        this.requestMethod = requestMethod;
        this.requestUri = requestUri;
        this.httpVersion = httpVersion;
    }

    public static RequestLine create(final String requestLine) {
        String[] parseValues = parseRequestLine(requestLine);

        RequestMethod requestMethod = RequestMethod.find(parseValues[REQUEST_METHOD_INDEX]);
        RequestUri requestUri = RequestUri.create(parseValues[REQUEST_URI_INDEX]);
        String httpVersion = parseValues[REQUEST_HTTP_VERSION_INDEX];

        return new RequestLine(requestMethod, requestUri, httpVersion);
    }

    private static String[] parseRequestLine(final String requestLine) {
        String[] requestValue = requestLine.split(BLANK);
        if (requestValue.length != REQUEST_LINE_LENGTH) {
            throw new InvalidRequestLineException();
        }

        return requestValue;
    }

    public String getRequestMethod() {
        return requestMethod.getValue();
    }

    public String getRequestUri() {
        return requestUri.getUri();
    }

    public String getRequestExtension() {
        String[] parseValues = requestUri.getUri().split(REQUEST_URI_EXTENSION_DELIMITER);
        if (parseValues.length != EXPECT_URI_EXTENSION_LENGTH) {
            throw new InvalidRequestExtensionException();
        }

        return parseValues[EXTENSION_INDEX];
    }

    public String getQueryParameterValue(final String parameter) {
        return requestUri.getQueryParameterValue(parameter);
    }

    public Map<String, String> getQueryParameters() {
        return requestUri.getQueryParameters();
    }

    public String getHttpVersion() {
        return httpVersion;
    }
}
