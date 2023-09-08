package org.apache.coyote.http11.common.request;

public class RequestLine {
    public static final String DELIMITER = " ";
    public static final int METHOD_IDX = 0;
    public static final int URI_IDX = 1;

    private final HttpMethod method;
    private final RequestUri uri;
    private final QueryParams params;

    private RequestLine(final HttpMethod method, final RequestUri uri, final QueryParams params) {
        this.method = method;
        this.uri = uri;
        this.params = params;
    }

    public static RequestLine create(final String line) {
        String[] targets = line.split(DELIMITER);

        HttpMethod method = HttpMethod.from(targets[METHOD_IDX]);

        String uriWithParams = targets[URI_IDX];
        RequestUri uri = RequestUri.create(uriWithParams);
        QueryParams params = QueryParams.create(uriWithParams);

        return new RequestLine(method, uri, params);
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
