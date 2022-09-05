package org.apache.coyote.common.request;

import java.util.Map;
import java.util.Optional;
import org.apache.coyote.common.HttpVersion;
import org.apache.coyote.common.request.parser.UrlParser;

public class Request {

    public static final String UNKNOWN_QUERY = "Could not find query string";

    private static final int METHOD = 0;
    private static final int URL = 1;
    private static final int HTTP_VERSION = 2;
    private static final String PATH_QUERY_STRING_DELIMITER = "?";

    private final HttpVersion httpVersion;
    private final RequestMethod method;
    private final String path;
    private final Map<String, String> queryString;

    public Request(final String request) {
        final String[] parsedRequest = request.split(" ");
        final String url = parsedRequest[URL];
        final int queryStringDelimiterIndex = url.indexOf(PATH_QUERY_STRING_DELIMITER);
        this.method = RequestMethod.of(parsedRequest[METHOD]);
        this.httpVersion = HttpVersion.of(parsedRequest[HTTP_VERSION]);
        this.path = UrlParser.getPath(url, queryStringDelimiterIndex);
        this.queryString = UrlParser.getQueryString(url, queryStringDelimiterIndex);
    }

    public String getPath() {
        return path;
    }

    public Optional<String> getQueryStringValue(final String key) {
        return Optional.ofNullable(queryString.get(key));
    }

    public String getRequestIdentifier() {
        if (queryString.size() != 0) {
            return String.join(" ", method.getValue(), path, PATH_QUERY_STRING_DELIMITER);
        }
        return String.join(" ", method.getValue(), path);
    }
}
