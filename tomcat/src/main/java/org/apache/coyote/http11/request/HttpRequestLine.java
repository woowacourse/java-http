package org.apache.coyote.http11.request;

public class HttpRequestLine {

    private static final int HTTP_METHOD_INDEX = 0;
    private static final int HTTP_URI_INDEX = 1;
    private static final int HTTP_VERSION_INDEX = 2;

    private final HttpMethod method;
    private final RequestURI requestURI;
    private final String version;

    public HttpRequestLine(String line) {
        String[] requestLine = line.split(" ");
        method = HttpMethod.fromName(requestLine[HTTP_METHOD_INDEX]);
        requestURI = new RequestURI(requestLine[HTTP_URI_INDEX]);
        version = requestLine[HTTP_VERSION_INDEX];
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return requestURI.getPath();
    }

    public String getVersion() {
        return version;
    }
}
