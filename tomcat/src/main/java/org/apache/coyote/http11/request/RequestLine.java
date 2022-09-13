package org.apache.coyote.http11.request;

public class RequestLine {

    private static final int METHOD_INDEX = 0;
    private static final int PATH_INDEX = 1;
    private static final int VERSION_INDEX = 2;

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
                requestLineProperties[METHOD_INDEX],
                requestLineProperties[PATH_INDEX],
                requestLineProperties[VERSION_INDEX]);
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
