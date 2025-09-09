package org.apache.coyote.http11;

public class RequestLine {

    private static final String QUERY_SEPARATOR = "?";
    private static final String ROOT_PATH = "/";
    private static final String EXTENSION_SEPARATOR = ".";

    private final String method;
    private final String path;
    private final String queryString;
    private final String protocolVersion;

    public RequestLine(String method, String path, String queryString, String protocolVersion) {
        this.method = method;
        this.path = path;
        this.queryString = queryString;
        this.protocolVersion = protocolVersion;
    }

    public static RequestLine from(String line) {
        String[] parts = line.split(" ");
        String method = parts[0];
        String uri = parts[1];
        String protocolVersion = parts[2];

        String path = uri;
        String queryString = "";
        int idx = uri.indexOf(QUERY_SEPARATOR);
        if (idx != -1) {
            path = uri.substring(0, idx);
            queryString = uri.substring(idx + 1);
        }

        return new RequestLine(method, path, queryString, protocolVersion);
    }

    public boolean hasQuery() {
        return queryString != null && !queryString.isEmpty();
    }

    public boolean isRootPath() {
        return ROOT_PATH.equals(path);
    }

    public String getExtension() {
        int index = path.lastIndexOf(EXTENSION_SEPARATOR);
        if (index == -1) {
            return "";
        }
        return path.substring(index + 1);
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getQueryString() {
        return queryString;
    }
}
