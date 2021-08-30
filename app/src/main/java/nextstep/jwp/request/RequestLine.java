package nextstep.jwp.request;

public class RequestLine {

    private static final String REQUEST_LINE_DELIMITER = " ";
    private static final String QUERY_STRING_STARTING_CHARACTER = "?";
    private static final String REQUEST_LINE_QUERY_STRING_DELIMITER = "\\?";
    private static final int HTTP_METHOD_INDEX = 0;
    private static final int REQUEST_PATH_INDEX = 1;
    private static final int VERSION_OF_PROTOCOL_INDEX = 2;
    private static final int REQUEST_PATH_INDEX_FOR_PATH_SPLIT = 0;
    private static final int QUERY_STRING_INDEX_FOR_PATH_SPLIT = 1;

    private final HttpMethod httpMethod;
    private final String requestPath;
    private final QueryStringParameters parameters;
    private final String versionOfProtocol;

    public RequestLine(HttpMethod httpMethod, String requestPath, QueryStringParameters parameters, String versionOfProtocol) {
        this.httpMethod = httpMethod;
        this.requestPath = requestPath;
        this.parameters = parameters;
        this.versionOfProtocol = versionOfProtocol;
    }

    public static RequestLine of(String requestLine) {
        if (hasQueryString(requestLine)) {
            return createRequestLineWithQueryString(requestLine);
        }
        return createRequestLine(requestLine);
    }

    public static RequestLine createRequestLine(String requestLine) {
        final String[] splitRequestLine = requestLine.split(REQUEST_LINE_DELIMITER);
        HttpMethod httpMethod = HttpMethod.of(splitRequestLine[HTTP_METHOD_INDEX]);
        String requestPath = splitRequestLine[REQUEST_PATH_INDEX];
        String versionOfProtocol = splitRequestLine[VERSION_OF_PROTOCOL_INDEX];
        QueryStringParameters parameters = QueryStringParameters.getEmptyParameters();
        return new RequestLine(httpMethod, requestPath, parameters, versionOfProtocol);
    }

    public static RequestLine createRequestLineWithQueryString(String requestLine) {
        final String[] splitRequestLine = requestLine.split(REQUEST_LINE_DELIMITER);
        final HttpMethod httpMethod = HttpMethod.of(splitRequestLine[HTTP_METHOD_INDEX]);
        final String requestPath = splitRequestLine[REQUEST_PATH_INDEX];
        final String[] splitRequestPath = requestPath.split(REQUEST_LINE_QUERY_STRING_DELIMITER);
        final String path = splitRequestPath[REQUEST_PATH_INDEX_FOR_PATH_SPLIT];
        final String queryString = splitRequestPath[QUERY_STRING_INDEX_FOR_PATH_SPLIT];
        final String versionOfProtocol = splitRequestLine[VERSION_OF_PROTOCOL_INDEX];
        final QueryStringParameters parameters = QueryStringParameters.of(queryString);
        return new RequestLine(httpMethod, path, parameters, versionOfProtocol);
    }

    private static boolean hasQueryString(String requestLine) {
        return requestLine.contains(QUERY_STRING_STARTING_CHARACTER);
    }
}
