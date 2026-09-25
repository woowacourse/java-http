package org.apache.coyote.http11;

public class RequestLine {
    private final String method;
    private final String requestPath;
    private final String protocolVersion;
    private final String pathUri;
    private final String queryString;

    public RequestLine(String method, String requestPath, String protocolVersion) {
        int queryIndex = requestPath.indexOf("?");

        this.method = method;
        this.requestPath = requestPath;
        this.protocolVersion = protocolVersion;
        this.pathUri = readPathUri(queryIndex);
        this.queryString = readQuery(queryIndex);
    }

    private String readPathUri(int queryIndex) {
        if (queryIndex == -1) {
            return requestPath;
        }
        return requestPath.substring(0, queryIndex);
    }

    private String readQuery(int queryIndex) {
        if (queryIndex == -1) {
            return "";
        }
        return requestPath.substring(queryIndex + 1);
    }

    public String getMethod() {
        return method;
    }

    public String getPathUri() {
        return pathUri;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    public String getQueryString() {
        return queryString;
    }
}
