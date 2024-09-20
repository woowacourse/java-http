package org.apache.coyote.http11.message.request;

public class RequestLine {

    private static final String QUERY_DELIMITER = "\\?";
    private static final int PATH_INDEX = 0;

    private final HttpMethod method;
    private final String uri;
    private final String protocolVersion;

    public RequestLine(HttpMethod method, String uri, String protocolVersion) {
        this.method = method;
        this.uri = uri;
        this.protocolVersion = protocolVersion;
    }

    public boolean isGet() {
        return method.isGet();
    }

    public boolean isPost() {
        return method.isPost();
    }

    public String getUri() {
        return uri;
    }

    public String getPath() {
        return uri.split(QUERY_DELIMITER)[PATH_INDEX];
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    @Override
    public String toString() {
        return "HttpLine{" +
                "method='" + method + '\'' +
                ", uri='" + uri + '\'' +
                ", protocolVersion='" + protocolVersion + '\'' +
                '}';
    }
}
