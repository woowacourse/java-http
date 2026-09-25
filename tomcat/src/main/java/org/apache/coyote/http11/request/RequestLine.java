package org.apache.coyote.http11.request;

import java.util.Map;

public class RequestLine {

    private static final String DELIMITER = " ";
    private static final String QUERY_DELIMITER = "?";
    private static final String PARAMETER_DELIMITER = "&";
    private static final int METHOD_INDEX = 0;
    private static final int URI_INDEX = 1;
    private static final int PROTOCOL_INDEX = 2;
    private static final int TOKEN_SIZE = 3;

    private final HttpMethod method;
    private final String path;
    private final Map<String, String> queryParameters;
    private final String protocol;

    private RequestLine(final HttpMethod method,
                       final String path,
                       final Map<String, String> queryParameters,
                       final String protocol) {
        this.method = method;
        this.path = path;
        this.queryParameters = queryParameters;
        this.protocol = protocol;
    }

    public static RequestLine from(final String line) {
        final String[] tokens = line.trim().split(DELIMITER);
        if (tokens.length != TOKEN_SIZE) {
            throw new IllegalArgumentException("요청 라인이 올바르지 않습니다." + line);
        }
        final String uri = tokens[URI_INDEX];
        return new RequestLine(
                HttpMethod.from(tokens[METHOD_INDEX]),
                parsePath(uri),
                parseQueryParameters(uri),
                tokens[PROTOCOL_INDEX]);
    }

    private static String parsePath(final String uri) {
        final int index = uri.indexOf(QUERY_DELIMITER);
        if (index == -1) {
            return uri;
        }
        return uri.substring(0, index);
    }

    private static Map<String, String> parseQueryParameters(final String uri) {
        final int index = uri.indexOf(QUERY_DELIMITER);
        if (index == -1) {
            return Map.of();
        }
        return ParameterParser.parse(uri.substring(index + 1), PARAMETER_DELIMITER);
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getQueryParameter(String name) {
        return queryParameters.get(name);
    }

    public String getProtocol() {
        return protocol;
    }
}
