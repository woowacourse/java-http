package org.apache.coyote.http.request;

public class RequestLine {

    private static final String URI_DELIMITER = "?";

    private final String method;
    private final String uri;
    private final String protocol;

    public RequestLine(String method, String path, String protocol) {
        this.method = method;
        this.uri = parseUri(path);
        this.protocol = protocol;
    }

    private static String parseUri(String path) {
        if (path.contains(URI_DELIMITER)) {
            int endIndex = path.indexOf(URI_DELIMITER);
            return path.substring(0, endIndex);
        }
        return path;
    }

    public String getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    @Override
    public String toString() {
        return "RequestLine{" +
               "method='" + method + '\'' +
               ", uri='" + uri + '\'' +
               ", protocol='" + protocol + '\'' +
               '}';
    }
}
