package org.apache.coyote.http11;

public class RequestLine {
    private static final String QUERY_DELIMITER = "?";

    private String method;
    private String path;
    private Parameters queryParameters;
    private String protocolVersion;

    public RequestLine(String requestLine) {
        String[] requestLineTokens = requestLine.split(" ");
        this.method = requestLineTokens[0];
        this.path = extractPath(requestLineTokens[1]);
        this.queryParameters = Parameters.from(extractQueryString(requestLineTokens[1]));
        this.protocolVersion = requestLineTokens[2];
    }

    private String extractPath(final String uri) {
        final int index = uri.indexOf(QUERY_DELIMITER);
        if (index == -1) {
            return uri;
        }
        return uri.substring(0, index);
    }

    private String extractQueryString(final String uri) {
        final int index = uri.indexOf(QUERY_DELIMITER);
        if (index == -1) {
            return "";
        }
        return uri.substring(index + 1);
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getQueryParameter(final String name) {
        return queryParameters.get(name);
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }
}
