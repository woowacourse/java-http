package org.apache.coyote.http11;

public class RequestLine {

    private final String method;
    private final String path;
    private final String version;

    private RequestLine(String method, String path, String version) {
        this.method = method;
        this.path = path;
        this.version = version;
    }

    public static RequestLine from(String firstLine) {
        String[] requestLineProperties = firstLine.split(" ");
        return new RequestLine(
                requestLineProperties[0], requestLineProperties[1], requestLineProperties[2]);
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getVersion() {
        return version;
    }

    @Override
    public String toString() {
        return "RequestLine{" +
                "method='" + method + '\'' +
                ", path='" + path + '\'' +
                ", version='" + version + '\'' +
                '}';
    }
}
