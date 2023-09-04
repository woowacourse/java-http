package org.apache.coyote.http11.common.request;

import org.apache.coyote.http11.common.HttpVersion;

public class StartLine {
    public static final String DELIMITER = " ";
    public static final int METHOD_IDX = 0;
    public static final int URI_IDX = 1;
    public static final int VERSION_IDX = 2;
    private final HttpMethod method;
    private final RequestUri uri;
    private final QueryParams params;
    private final HttpVersion version;

    private StartLine(final HttpMethod method, final RequestUri uri, final QueryParams params,
                      final HttpVersion version) {
        this.method = method;
        this.uri = uri;
        this.params = params;
        this.version = version;
    }

    public static StartLine create(final String line) {
        String[] targets = line.split(DELIMITER);

        HttpMethod method = HttpMethod.valueOf(targets[METHOD_IDX]);

        String uriWithParams = targets[URI_IDX];
        RequestUri uri = RequestUri.create(uriWithParams);
        QueryParams params = QueryParams.create(uriWithParams);

        HttpVersion httpVersion = HttpVersion.fromDetail(targets[VERSION_IDX]);

        return new StartLine(method, uri, params, httpVersion);
    }

    public RequestUri getUri() {
        return uri;
    }

    public QueryParams getParams() {
        return params;
    }

    public HttpMethod getMethod() {
        return method;
    }
}
