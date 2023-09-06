package org.apache.coyote.http11.request.uri;

import static org.apache.coyote.http11.common.Constant.QUERY_PARAMS_LETTER;
import static org.apache.coyote.http11.common.Constant.RESOURCE_LETTER;
import static org.apache.coyote.http11.common.Constant.URI_SEPARATOR;

public class Uri {

    private static final int HTTP_METHOD_INDEX = 0;
    private static final int PATH_INDEX = 1;
    private static final int HTTP_VERSION_INDEX = 2;

    private final HttpMethod httpMethod;
    private final String path;
    private final HttpVersion httpVersion;

    private Uri(final HttpMethod httpMethod, final String path, final HttpVersion httpVersion) {
        this.httpMethod = httpMethod;
        this.path = path;
        this.httpVersion = httpVersion;
    }

    public static Uri from(final String requestLines) {
        String[] uriComponents = requestLines.split(URI_SEPARATOR);

        return new Uri(
                HttpMethod.from(uriComponents[HTTP_METHOD_INDEX]),
                uriComponents[PATH_INDEX],
                HttpVersion.from(uriComponents[HTTP_VERSION_INDEX])
        );
    }

    public boolean hasQueryParams() {
        return path.contains(QUERY_PARAMS_LETTER);
    }

    public String getQueryParams() {
        int queryIndex = path.indexOf(QUERY_PARAMS_LETTER);
        return path.substring(queryIndex + 1);
    }

    public boolean hasResource() {
        return path.contains(RESOURCE_LETTER);
    }

    public boolean isGetMethod() {
        return httpMethod.equals(HttpMethod.GET);
    }

    public boolean isPostMethod() {
        return this.httpMethod.equals(HttpMethod.POST);
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getPath() {
        return path;
    }

    public String getHttpVersion() {
        return httpVersion.getVersion();
    }
}
