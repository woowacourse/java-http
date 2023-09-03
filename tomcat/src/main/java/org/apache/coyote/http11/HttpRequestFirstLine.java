package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.List;

public class HttpRequestFirstLine {

    private final String method;
    private final String path;
    private final String protocolVersion;

    public static HttpRequestFirstLine from(final String firstLine) {
        final List<String> firstLines = Arrays.asList(firstLine.split(" "));
        return new HttpRequestFirstLine(firstLines.get(0), firstLines.get(1), firstLines.get(2));
    }

    public HttpRequestFirstLine(
            final String method,
            final String path,
            final String protocolVersion
    ) {
        this.method = method;
        this.path = path;
        this.protocolVersion = protocolVersion;
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }
    
}
