package org.apache.coyote.http11.request;

public class RequestLine {

    private final HttpMethod method;
    private final String path;
    private final String protocol;

    private RequestLine(final HttpMethod method, final String path, final String protocol) {
        this.method = method;
        this.path = path;
        this.protocol = protocol;
    }

    public static RequestLine from(final String line) {
        final String[] splitLine = line.split(" ");
        return new RequestLine(HttpMethod.valueOf(splitLine[0]), splitLine[1], splitLine[2]);
    }

    public String getMethod() {
        return method.name();
    }

    public String getPath() {
        return path;
    }

    public String getProtocol() {
        return protocol;
    }
}
