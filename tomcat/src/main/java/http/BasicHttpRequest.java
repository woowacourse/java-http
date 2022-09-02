package http;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BasicHttpRequest implements HttpRequest {

    private static final String WINDOWS_LINE_SEPARATOR = "\r\n";
    private static final String LINUX_LINE_SEPARATOR = "\n";
    private static final int BODY_SEPARATOR_BY_LINE_SEPARATOR = 2;
    private static final int INDEX_FOR_HTTP_HEADER = 0;
    private static final int INDEX_FOR_FIRST_LINE_OF_REQUEST = 0;
    private static final String SINGLE_WHITE_SPACE = " ";
    private static final int INDEX_FOR_HTTP_METHOD = 0;
    private static final int INDEX_FOR_REQUEST_URI = 1;
    private static final int INDEX_FOR_PROTOCOL = 2;
    private static final int INDEX_FOR_SKIPPING_FIRST_LINE = 1;
    private static final String COLON_WITH_WHITE_SPACE_TO_SPLIT_HEADER = ": ";
    private static final int INDEX_FOR_HTTP_BODY = 1;
    private static final int TWO_LENGTH_WHICH_CONTAINS_BODY = 2;
    private static final String DOT_CHARACTER = ".";
    private static final int ONE_TO_IGNORE_DOT = 1;
    private static final String QUESTION_MARK = "?";
    private static final String EQUALS_MARK = "=";

    private final Map<String, String> headers;
    private final Map<String, Object> queryParameters;
    private final String body;

    public BasicHttpRequest(final Map<String, String> headers, final Map<String, Object> queryParameters,
                            final String body) {
        this.headers = headers;
        this.queryParameters = queryParameters;
        this.body = body;
    }

    public static BasicHttpRequest from(String httpRequest) {
        final var lineSeparator = identifyLineSeparator(httpRequest);
        final var splitHttpRequest = httpRequest.split(lineSeparator.repeat(BODY_SEPARATOR_BY_LINE_SEPARATOR));

        final var headers = parseHeaders(splitHttpRequest[INDEX_FOR_HTTP_HEADER], lineSeparator);
        final var queryParameters = parseQueryParameters(headers.get(HttpHeader.REQUEST_URI.getName()));
        final var body = parseBody(splitHttpRequest);

        return new BasicHttpRequest(headers, queryParameters, body);
    }

    private static String identifyLineSeparator(final String httpRequest) {
        if (httpRequest.contains(WINDOWS_LINE_SEPARATOR)) {
            return WINDOWS_LINE_SEPARATOR;
        }

        return LINUX_LINE_SEPARATOR;
    }

    private static Map<String, String> parseHeaders(final String httpHeader, final String lineSeparator) {
        final var parsedHttpHeader = Arrays.stream(httpHeader.split(lineSeparator))
                .collect(Collectors.toList());

        final var headers = new HashMap<String, String>();
        putFirstLine(parsedHttpHeader, headers);
        putOtherLines(parsedHttpHeader, headers);

        return headers;
    }

    private static void putFirstLine(final List<String> parsedHttpHeader, final Map<String, String> headers) {
        final var splitFirstLine = parsedHttpHeader.get(INDEX_FOR_FIRST_LINE_OF_REQUEST).split(SINGLE_WHITE_SPACE);

        headers.put(HttpHeader.METHOD.getName(), splitFirstLine[INDEX_FOR_HTTP_METHOD]);
        headers.put(HttpHeader.REQUEST_URI.getName(), splitFirstLine[INDEX_FOR_REQUEST_URI]);
        headers.put(HttpHeader.PROTOCOL.getName(), splitFirstLine[INDEX_FOR_PROTOCOL]);
    }

    private static void putOtherLines(final List<String> parsedHttpHeader, final Map<String, String> headers) {
        for (int i = INDEX_FOR_SKIPPING_FIRST_LINE; i < parsedHttpHeader.size(); i++) {
            final String headerInfo = parsedHttpHeader.get(i);
            final int firstColonIndex = headerInfo.indexOf(COLON_WITH_WHITE_SPACE_TO_SPLIT_HEADER);

            headers.put(
                    headerInfo.substring(0, firstColonIndex),
                    headerInfo.substring(firstColonIndex + 1)
            );
        }
    }

    private static Map<String, Object> parseQueryParameters(final String requestUri) {
        if (!requestUri.contains(QUESTION_MARK)) {
            return new HashMap<>();
        }

        final var queryParameterUri = requestUri.substring(requestUri.indexOf(QUESTION_MARK) + 1);
        final String[] queryParameterPairs = queryParameterUri.split("&");

        final var result = new HashMap<String, Object>();
        for (String queryParameterPair : queryParameterPairs) {
            addParameter(result, queryParameterPair);
        }

        return result;
    }

    private static void addParameter(final Map<String, Object> queryParameters, final String queryParameterPair) {
        final var splitQueryParameterPair = queryParameterPair.split(EQUALS_MARK);
        final var queryParameterKey = splitQueryParameterPair[0];
        final var queryParameterValue = splitQueryParameterPair[1];

        if (queryParameters.containsKey(queryParameterKey)) {
            final var collection = (Collection) queryParameters.get(queryParameterKey);
            collection.add(queryParameterValue);
            return;
        }

        queryParameters.put(queryParameterKey, queryParameterValue);
    }

    private static String parseBody(final String[] splitHttpRequest) {
        if (splitHttpRequest.length < TWO_LENGTH_WHICH_CONTAINS_BODY) {
            return null;
        }

        return splitHttpRequest[INDEX_FOR_HTTP_BODY];
    }

    @Override
    public HttpMethod getHttpMethod() {
        return HttpMethod.from(headers.get(HttpHeader.METHOD.getName()));
    }

    @Override
    public String getRequestURI() {
        return headers.get(HttpHeader.REQUEST_URI.getName());
    }

    @Override
    public String getRequestURIWithoutQueryParams() {
        final var requestURI = getRequestURI();
        if (requestURI.contains(QUESTION_MARK)) {
            return requestURI.substring(0, requestURI.indexOf(QUESTION_MARK));
        }

        return requestURI;
    }

    @Override
    public String getProtocol() {
        return headers.get(HttpHeader.PROTOCOL.getName());
    }

    @Override
    public String getHost() {
        return headers.get(HttpHeader.HOST.getName());
    }

    @Override
    public String getConnection() {
        return headers.get(HttpHeader.CONNECTION.getName());
    }

    @Override
    public Map<String, String> getHeaders() {
        return new HashMap<>(this.headers);
    }

    @Override
    public String getBody() {
        return this.body;
    }

    @Override
    public String getContentType() {
        final var requestURI = getRequestURI();
        final var indexOfDot = requestURI.lastIndexOf(DOT_CHARACTER);
        final var suffix = requestURI.substring(indexOfDot + ONE_TO_IGNORE_DOT);

        return ContentType.from(suffix).getName();
    }

    @Override
    public Object getParameter(final String key) {
        return this.queryParameters.get(key);
    }

    @Override
    public Map<String, Object> getParameters() {
        return new HashMap<>(this.queryParameters);
    }
}
