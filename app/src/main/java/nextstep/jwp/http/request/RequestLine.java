package nextstep.jwp.http.request;

import nextstep.jwp.http.ContentType;
import nextstep.jwp.http.HttpMethod;
import nextstep.jwp.util.QueryParamsParser;

import java.util.HashMap;
import java.util.Map;

public class RequestLine {
    private static final String SPACE_DELIMITER = " ";
    private static final String QUERY_PARAMETER_DELIMITER = "?";
    private static final int METHOD_INDEX = 0;
    private static final int PATH_INDEX = 1;
    private static final int PROTOCOL_INDEX = 2;

    private final HttpMethod httpMethod;
    private final String path;
    private final String protocol;
    private final Map<String, String> queryParameters;

    public RequestLine(final String requestLine) {
        final String[] requestHeaderFirstLine = requestLine.split(SPACE_DELIMITER);
        final HttpMethod requestHttpMethod = HttpMethod.valueOf(requestHeaderFirstLine[METHOD_INDEX]);
        final String requestPath = requestHeaderFirstLine[PATH_INDEX];
        final String requestProtocol = requestHeaderFirstLine[PROTOCOL_INDEX];
        final Map<String, String> requestQueryParameters = parseQueryParameters(requestPath);

        this.httpMethod = requestHttpMethod;
        this.path = adjustPath(requestPath);
        this.protocol = requestProtocol;
        this.queryParameters = requestQueryParameters;
    }

    private Map<String, String> parseQueryParameters(final String uri) {
        final Map<String, String> newQueryParameters = new HashMap<>();

        final int index = uri.indexOf(QUERY_PARAMETER_DELIMITER);
        if (index != -1) {
            final String queryString = uri.substring(index + 1);
            QueryParamsParser.parse(queryString, newQueryParameters);
        }

        return newQueryParameters;
    }

    private String adjustPath(final String path) {
        if ("/".equals(path)) {
            return "/index.html";
        }

        final ContentType contentType = ContentType.findByUrl(path);
        final int index = path.indexOf(QUERY_PARAMETER_DELIMITER);
        if (contentType.hasFileExtension() || index == -1) {
            return path;
        }

        return path.substring(0, index + 1);
    }

    public boolean doesNotHaveQueryParameters() {
        return queryParameters.isEmpty();
    }

    public HttpMethod getHttpMethod() {
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
}
