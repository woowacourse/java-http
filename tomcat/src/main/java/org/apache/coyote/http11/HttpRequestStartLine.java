package org.apache.coyote.http11;

public class HttpRequestStartLine {

    private static final int INDEX_OF_METHOD = 0;
    private static final int INDEX_OF_URI = 1;
    private static final int INDEX_OF_VERSION = 2;

    private final String method;
    private final String uri;
    private final String version;

    public static HttpRequestStartLine from(String line) {
        String[] parsedLine = line.split(" ");

        return new HttpRequestStartLine(
            parsedLine[INDEX_OF_METHOD],
            parsedLine[INDEX_OF_URI],
            parsedLine[INDEX_OF_VERSION]
        );
    }

    private HttpRequestStartLine(String method, String uri, String version) {
        this.method = method;
        this.uri = uri;
        this.version = version;
    }

    public String getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    public String getVersion() {
        return version;
    }
}
