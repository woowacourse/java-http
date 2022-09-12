package org.apache.coyote.http11.request;

import org.apache.coyote.http11.Protocol;
import org.apache.coyote.http11.URL;

public class StartLine {

    private static final String SPACE_DELIMITER = " ";
    private static final int METHOD_INDEX = 0;
    private static final int URI_INDEX = 1;
    private static final String QUERY_PARAM_DELIMITER = "?";
    private static final String QUERY_PARAM_DELIMITER_REGEX = "\\?";
    private static final int VERSION_INDEX = 2;
    private static final int PARAM_INDEX = 1;

    private final HttpMethod method;
    private final Protocol version;
    private final URL requestURL;
    private final QueryParams params;

    private StartLine(final HttpMethod method, final Protocol version, final URL requestURL, final QueryParams params) {
        this.method = method;
        this.version = version;
        this.requestURL = requestURL;
        this.params = params;
    }

    public static StartLine of(final String startLine) {
        final String[] splitStartLine = startLine.split(SPACE_DELIMITER);
        final HttpMethod httpMethod = HttpMethod.of(splitStartLine[METHOD_INDEX]);
        final URL url = URL.of(splitStartLine[URI_INDEX]);
        final QueryParams params = parseQueryParams(splitStartLine[URI_INDEX]);
        final Protocol protocol = Protocol.of(splitStartLine[VERSION_INDEX]);
        return new StartLine(httpMethod, protocol, url, params);
    }

    private static QueryParams parseQueryParams(final String parsedUrl) {
        if (!parsedUrl.contains(QUERY_PARAM_DELIMITER)) {
            return QueryParams.ofEmpty();
        }
        final String urlQueryParams = parsedUrl.split(QUERY_PARAM_DELIMITER_REGEX)[PARAM_INDEX];
        return QueryParams.of(urlQueryParams);
    }

    public boolean isForStaticFile() {
        return requestURL.isForStaticFile();
    }

    public boolean isDefault() {
        return requestURL.isDefault();
    }

    public boolean hasPath(final String path) {
        return requestURL.hasPath(path);
    }

    public HttpMethod getMethod() {
        return method;
    }

    public URL getURL() {
        return requestURL;
    }
}
