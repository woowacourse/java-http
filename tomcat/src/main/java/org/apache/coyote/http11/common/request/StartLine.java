package org.apache.coyote.http11.common.request;

import org.apache.coyote.http11.common.HttpVersion;

public class StartLine {
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
        String[] targets = line.split(" ");

        HttpMethod method = HttpMethod.valueOf(targets[0]);

        String uriWithParams = targets[1];
        RequestUri uri = RequestUri.create(uriWithParams);
        QueryParams params = QueryParams.create(uriWithParams);

        HttpVersion httpVersion = HttpVersion.fromDetail(targets[2]);

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
