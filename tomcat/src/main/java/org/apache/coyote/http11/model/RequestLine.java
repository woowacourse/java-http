package org.apache.coyote.http11.model;

public class RequestLine {

    private final HttpMethod httpMethod;
    private final String path;
    private final String httpVersion;
    private final QueryParameter queryParameter;

    private RequestLine(final HttpMethod httpMethod,
                        final String path,
                        final String httpVersion,
                        final QueryParameter queryParameter
    ) {
        this.httpMethod = httpMethod;
        this.path = path;
        this.httpVersion = httpVersion;
        this.queryParameter = queryParameter;
    }

    public static RequestLine from(final String requestLine) {
        final var requestLineChunks = requestLine.split(" ");

        if (requestLineChunks.length < 2) {
            throw new IllegalArgumentException("Invalid request line : " + requestLine);
        }

        final var httpMethod = HttpMethod.valueOf(requestLineChunks[0]);
        final var requestUri = requestLineChunks[1];
        final var httpVersion = requestLineChunks[2];

        final var queryDelimiterIndex = requestUri.indexOf("?");

        String path = requestUri;
        QueryParameter queryParameter = new QueryParameter();

        if (queryDelimiterIndex != -1) {
            path = requestUri.substring(0, queryDelimiterIndex);
            queryParameter = new QueryParameter(requestUri.substring(queryDelimiterIndex + 1));
        }

        return new RequestLine(httpMethod, path, httpVersion, queryParameter);
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getPath() {
        return path;
    }

    public String getQueryParameterValue(String key) {
        return queryParameter.getValue(key);
    }

    public void mergeToQueryParameter(final String body) {
        final QueryParameter bodyParameter = QueryParameter.fromBody(body);
        queryParameter.merge(bodyParameter);
    }
}
