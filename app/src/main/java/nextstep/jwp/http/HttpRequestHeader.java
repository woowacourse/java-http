package nextstep.jwp.http;

import java.util.*;

public class HttpRequestHeader {
    public static final String CONTENT_LENGTH_HEADER = "Content-Length: ";

    private final String httpMethod;
    private final String protocol;
    private final String path;
    private final Map<String, String> queryParameters;
    private final int contentLength;

    public HttpRequestHeader(final List<String> requestHeaders) {
        if (requestHeaders.isEmpty()) {
            throw new IllegalStateException();
        }

        final String[] requestHeaderFirstLine = requestHeaders.get(0).split(" ");
        final String httpMethod = requestHeaderFirstLine[0];
        final String protocol = requestHeaderFirstLine[2];
        final String uri = requestHeaderFirstLine[1];
        final Map<String, String> queryParameters = parseQueryParameters(uri);
        final int contentLength = parseContentLength(requestHeaders);

        this.httpMethod = httpMethod;
        this.protocol = protocol;
        this.path = trimPath(uri);;
        this.queryParameters = queryParameters;
        this.contentLength = contentLength;
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

    private String trimPath(final String uri) {
        if ("/".equals(uri)) {
            return "/index.html";
        }
        if (!uri.endsWith("html")) {
            return uri + ".html";
        }
        return uri;
    }

    private Map<String, String> parseQueryParameters(final String uri) {
        final Map<String, String> newQueryParameters = new HashMap<>();

        final int index = uri.indexOf("?");
        if (index != -1) {
            final String queryString = uri.substring(index + 1);
            addQueryParameters(newQueryParameters, queryString);
        }

        return newQueryParameters;
    }

    private void addQueryParameters(final Map<String, String> newQueryParameters, final String queryString) {
        Arrays.stream(queryString.split("&"))
                .map(query -> query.split("="))
                .forEach(queryPair -> {
                    newQueryParameters.put(queryPair[0], queryPair[1]);
                });
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
