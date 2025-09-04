package org.apache.coyote.util;

public record RequestLine(
        String method,
        String path,
        QueryParameters queryParameters,
        String httpVersion
) {

    public RequestLine(String method, String path, String httpVersion) {
        this(method, path, QueryParameters.createEmpty(), httpVersion);
    }

    public static RequestLine parseFrom(final String rawRequestLine) {
        String[] requestLineParts = rawRequestLine.split(" ");
        String method = requestLineParts[0];
        String requestUrl = requestLineParts[1];
        String httpVersion = requestLineParts[2];
        if (!requestUrl.contains("?")) {
            return new RequestLine(method, requestUrl, httpVersion);
        }
        String[] pathAndQuery = requestUrl.split("\\?", 2);
        String requestPath = pathAndQuery[0];
        String queryString = pathAndQuery[1];

        QueryParameters queryParameters = QueryParameters.parseFrom(queryString);
        return new RequestLine(method, requestPath, queryParameters, httpVersion);
    }
}
