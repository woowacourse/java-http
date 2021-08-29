package nextstep.jwp.http;

import java.util.*;

import static nextstep.jwp.http.HttpRequest.*;

public class HttpRequestHeader {
    private static final String CONTENT_LENGTH_HEADER = "Content-Length: ";
    private static final String QUERY_PARAMETER_DELIMITER = "?";
    private static final String SPACE = " ";

    private final String httpMethod;
    private final String protocol;
    private final String path;
    private final Map<String, String> queryParameters;
    private final int contentLength;

    public HttpRequestHeader(final List<String> requestHeaders) {
        if (requestHeaders.isEmpty()) {
            throw new IllegalStateException();
        }

        final String[] requestHeaderFirstLine = requestHeaders.get(0).split(SPACE);
        final String requestHttpMethod = requestHeaderFirstLine[0];
        final String requestProtocol = requestHeaderFirstLine[2];
        final String requestUri = requestHeaderFirstLine[1];
        final Map<String, String> requestQueryParameters = parseQueryParameters(requestUri);
        final int requestContentLength = parseContentLength(requestHeaders);

        this.httpMethod = requestHttpMethod;
        this.protocol = requestProtocol;
        this.path = trimPath(requestUri, requestHttpMethod);
        this.queryParameters = requestQueryParameters;
        this.contentLength = requestContentLength;
    }

    private int parseContentLength(final List<String> requestHeaders) {
        final String contentLengthHeader = requestHeaders.stream()
                .filter(header -> header.startsWith(CONTENT_LENGTH_HEADER))
                .findFirst()
                .orElseGet(() -> null);

        if (contentLengthHeader == null) {
            return 0;
        }

        final String contentLengthValue = contentLengthHeader.substring(CONTENT_LENGTH_HEADER.length());
        return Integer.parseInt(contentLengthValue);
    }

    private Map<String, String> parseQueryParameters(final String uri) {
        final Map<String, String> newQueryParameters = new HashMap<>();

        final int index = uri.indexOf(QUERY_PARAMETER_DELIMITER);
        if (index != -1) {
            final String queryString = uri.substring(index + 1);
            addQueryParameters(newQueryParameters, queryString);
        }

        return newQueryParameters;
    }

    private String trimPath(final String path, final String httpMethod) {
        if ("/".equals(path)) {
            return "/index.html";
        }

        ContentType contentType = ContentType.findByUrl(path);
        if (!contentType.equals(ContentType.NONE) || HttpMethod.isPost(httpMethod)) {
            return path;
        }

        final int index = path.indexOf(QUERY_PARAMETER_DELIMITER);
        if (index != -1) {
            return path.substring(0, index + 1);
        }

        return path + ".html";
    }

    private void addQueryParameters(final Map<String, String> newQueryParameters, final String queryString) {
        Arrays.stream(queryString.split(QUERY_STRING_DELIMITER))
                .map(query -> query.split(QUERY_KEY_VALUE_DELIMITER))
                .forEach(queryPair -> newQueryParameters.put(queryPair[KEY_INDEX], queryPair[VALUE_INDEX]));
    }

    public boolean hasNoQueryParameters() {
        return queryParameters.isEmpty();
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getQueryParameters() {
        return queryParameters;
    }

    public int getContentLength() {
        return contentLength;
    }
}
